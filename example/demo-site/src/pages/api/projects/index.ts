import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../auth/[...nextauth]";
import { coreSubscriptionApi } from '@/lib/core-subscription-api';
import type { NextApiRequest, NextApiResponse } from 'next'
import formidable from 'formidable';
import fs from 'fs';
import { S3Client, PutObjectCommand } from "@aws-sdk/client-s3";
import { nanoid } from "nanoid";
import { coreVerificationApi } from "@/lib/core-verification-api";
import { ListProjects200ResponseCategoryEnum } from "@/lib/ryp-subscription-api";

// Configure AWS S3
const s3Client = new S3Client({
  credentials: {
    accessKeyId: process.env.CDN_AWS_ACCESS_KEY_ID!,
    secretAccessKey: process.env.CDN_AWS_SECRET_ACCESS_KEY!,
  },
  region: process.env.CDN_AWS_REGION!,
});
 
export const config = {
  api: {
    bodyParser: false,
  },
};

// Parse form data as a promise because formidable uses a callback
const parseForm = (req: NextApiRequest): Promise<{ fields: formidable.Fields<string>, files: formidable.Files<string> }> =>
  new Promise((resolve, reject) => {
    const form = formidable({ });

    form.parse(req, (err, fields, files) => {
      if (err) {
        return reject(err);
      }
      resolve({ fields, files });
    });
  });

const validateFile = (file: formidable.File) => {
  const allowedTypes = ['image/png', 'image/jpeg', 'image/webp', 'image/svg+xml'];
  if (!file.mimetype || !allowedTypes.includes(file.mimetype)) {
    throw new Error('Invalid file type. Only PNG, JPG, WEBP, and SVG are allowed.');
  }
};

export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse
) {
  const session = await getServerSession(
    req,
    res,
    getNextAuthOptions(req, res)
  );
  
  if (req.method === 'POST' && session?.userId) {
    const accountId = session.userId;
    try {
      const { fields, files } = await parseForm(req);
      const file = files.logo?.[0];
      if (file) {
        const newProject = await createProject(fields, accountId);
        const originalExtension = file.originalFilename?.split('.').pop() ?? 'png';
        const fileStream = fs.createReadStream(file.filepath);
    
        validateFile(file);

        const s3Name = `projects/${newProject.data.id}/${nanoid()}.${originalExtension}`;
        const uploadParams = {
          Bucket: process.env.CDN_AWS_S3_BUCKET_NAME!,
          Key: s3Name,
          Body: fileStream,
          ContentType: file.mimetype!,
        };

        try { // Nested try since we will ignore failed uploads and simply let them upload again later if this happens, instead of holding up the process.
          // TODO create several blobs for different sizes
          const uploadCommand = new PutObjectCommand(uploadParams);
          await s3Client.send(uploadCommand);
          const updatedProject = await coreSubscriptionApi.updateProject(newProject.data.id, {
            logo: s3Name,
          });
          res.status(updatedProject.status).json(updatedProject.data);
        } catch (err) {
          console.error(err);
          res.status(newProject.status).json(newProject.data);
        }
      }
    } catch(err: any) {
      console.error(err);
      res.status(500).json({ error: `${err}` });
    };
  } else if (req.method === 'GET') {
    const response = await coreSubscriptionApi.listProjects();
    res.status(response.status).json(response.data);
  }
}

async function createProject(fields: formidable.Fields<string>, accountId: number) {
  const project = JSON.parse(fields.project?.[0]!);
  project.logo = '';
  if (fields.initialStakepool) {
    const stakepoolProject = await prepareStakepoolProject(fields);
    return coreSubscriptionApi.addNewProject(stakepoolProject, accountId);
  } else {
    return coreSubscriptionApi.addNewProject(project, accountId);
  }
}

async function prepareStakepoolProject(fields: formidable.Fields<string>) {
  const stakepoolInfo = JSON.parse(fields.initialStakepool?.[0]!);
  const finalizeVerification = await coreVerificationApi.completeStakepoolVerification(stakepoolInfo.verification.poolHash, stakepoolInfo.verification.nonce, stakepoolInfo.verification);
  if (finalizeVerification.status !== 200) {
    throw new Error('Failed to verify stakepool');
  }
  const stakepoolDetails = (await coreVerificationApi.getStakepoolDetails(stakepoolInfo.verification.poolHash)).data;
  const stakepoolProject = {
    id: 0,
    category: ListProjects200ResponseCategoryEnum.Spo,
    name: stakepoolDetails.name,
    url: stakepoolDetails.homepage,
    description: stakepoolDetails.description,
    logo: '',
    stakepools: [{
      poolHash: (stakepoolInfo.verification.poolHash as string),
      verificationNonce: (stakepoolInfo.verification.nonce as string),
      verificationTime: new Date().toISOString(),
    }],
    manuallyVerified: new Date().toISOString(), // Automatically mark the stakepool project as verified
  };
  return stakepoolProject;
}

