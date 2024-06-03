import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../auth/[...nextauth]";
import { coreSubscriptionApi } from '@/lib/core-subscription-api';
import type { NextApiRequest, NextApiResponse } from 'next'
import formidable from 'formidable';
import fs from 'fs';
import { S3Client, PutObjectCommand } from "@aws-sdk/client-s3";
import { nanoid } from "nanoid";

// Configure AWS S3
const s3Client = new S3Client({
  credentials: {
    accessKeyId: process.env.AWS_ACCESS_KEY_ID!,
    secretAccessKey: process.env.AWS_SECRET_ACCESS_KEY!,
  },
  region: process.env.AWS_REGION!,
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
        const project = JSON.parse(fields.project?.[0]!);
        project.logo = '';
        const newProject = await coreSubscriptionApi.addNewProject(project, accountId);
        const originalExtension = file.originalFilename?.split('.').pop() ?? 'png';
        const fileStream = fs.createReadStream(file.filepath);
    
        validateFile(file);

        const s3Name = `projects/${newProject.data.id}/${nanoid()}.${originalExtension}`;
        const uploadParams = {
          Bucket: process.env.AWS_S3_BUCKET_NAME!,
          Key: s3Name,
          Body: fileStream,
          ContentType: file.mimetype!,
        };

        // TODO create several blobs for different sizes

        const uploadCommand = new PutObjectCommand(uploadParams);
        await s3Client.send(uploadCommand);
        const updatedProject = await coreSubscriptionApi.updateProject(newProject.data.id, {
          logo: s3Name,
        });
        res.status(updatedProject.status).json(updatedProject.data);
      }
    } catch(err: any) {
      res.status(500).json({ error: `${err}` });
    };
  } else if (req.method === 'GET') {
    const response = await coreSubscriptionApi.listProjects();
    res.status(response.status).json(response.data);
  }
}