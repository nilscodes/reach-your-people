import sendgrid from "@sendgrid/mail"
import mjml2html from "mjml";
import { Theme } from "next-auth";

sendgrid.setApiKey(process.env.AUTH_SENDGRID_KEY ?? '');

const baseUrl = process.env.NEXTAUTH_URL;

const magicLinkTemplate = `<mjml>
  <mj-head>
    <mj-title>{{title}}</mj-title>
    <mj-attributes>
      <mj-font name="Roboto" href="https://fonts.googleapis.com/css?family=Roboto:400,700"></mj-font>
      <mj-all font-family="Roboto, 'Helvetica Neue', Helvetica, Arial, sans-serif"></mj-all>
      <mj-class name="footer-text" color="#ffffff" font-size="12px"></mj-class>
    </mj-attributes>
  </mj-head>
  <mj-body background-color="#eeeeee">
    <mj-section background-color="#ffffff">
      <mj-column>
        <mj-image width="600px" src="{{headerImageUrl}}" alt="RYP Logo"></mj-image>
      </mj-column>
    </mj-section>
    
    <mj-section background-color="#ffffff">
      <mj-column>
        <mj-text font-size="24px" font-weight="bold" color="#222222">
          {{title}}
        </mj-text>
        <mj-text font-size="16px" color="#444444">
          {{announcementBody}}
        </mj-text>
        <mj-text font-size="12px" color="#444444">
          {{footnote}}
        </mj-text>
      </mj-column>
    </mj-section>

    <mj-section background-color="#ffffff">
      <mj-column>
        <mj-button background-color="#101C2E" color="#FFFFFF" href="{{ctaUrl}}" font-size="16px" padding="10px 25px" font-weight="bold">
          {{ctaText}}
        </mj-button>
      </mj-column>
    </mj-section>
    
    <mj-section background-url="{{footerImageUrl}}">
      <mj-column>
        <mj-text mj-class="footer-text" align="center" color="#eeeeee">
          © 2024 Vibrant Solutions LLC, All Rights Reserved<br>
          30 N Gould St STE R, Sheridan, WY 82801, USA
        </mj-text>
        <mj-text mj-class="footer-text" align="center">
          <a href="{{unsubscribeLink}}" style="color:#eeeeee; text-decoration:none;">Unsubscribe</a> | <a href="{{preferencesLink}}" style="color:#eeeeee; text-decoration:none;">Manage Preferences</a>
        </mj-text>
      </mj-column>
    </mj-section>
  </mj-body>
</mjml>`

export async function sendVerificationRequest(params: any) {
  const { identifier, url, provider, theme } = params
  const { host } = new URL(url)
  try {
    const msg = {
      to: identifier,
      from: { name: 'Reach Your People (RYP)', email: provider.from },
      subject: `Sign in to ${host}`,
      text: text({ url, host }),
      html: html({ identifier, url, host, theme }),
    }
    await sendgrid.send(msg);
  } catch (error: any) {
    console.log(error);
    throw new Error(`Verification email to ${identifier} could not be sent`)
  }
}

/**
 * Email HTML body
 * Insert invisible space into domains from being turned into a hyperlink by email
 * clients like Outlook and Apple mail, as this is confusing because it seems
 * like they are supposed to click on it to sign in.
 *
 * @note We don't add the email address to avoid needing to escape it, if you do, remember to sanitize it!
 */
function html(params: { identifier: string, url: string, host: string, theme: Theme }) {
  const { identifier, url, host, theme } = params

  const escapedHost = host.replace(/\./g, "&#8203;.")

  const placeholders = {
    headerImageUrl: `${baseUrl}/email_header_dark.png`,
    title: `Sign in to ${escapedHost}`,
    unsubscribeLink: `${baseUrl}/login/mail/unsubscribe?email=${identifier}`,
    preferencesLink: `${baseUrl}/account`,
    announcementBody: 'You are receiving this email because someone requested a sign in link for Reach Your People (RYP) for this email address.',
    footnote: 'If you did not request this email you can safely ignore it.',
    footerImageUrl: `${baseUrl}/email_footer_dark.png`,
    ctaText: 'Sign in to ' + escapedHost,
    ctaUrl: url,
  } as any;

  let renderedTemplate = magicLinkTemplate;
  Object.keys(placeholders).forEach(key => {
    const regex = new RegExp(`{{${key}}}`, 'g');
    renderedTemplate = renderedTemplate.replace(regex, placeholders[key]);
  });
  const { html, errors } = mjml2html(renderedTemplate);

  if (errors.length > 0) {
    console.error(errors);
    throw new Error('Error rendering email template');
  }
  return html;
}

/** Email Text body (fallback for email clients that don't render HTML, e.g. feature phones) */
function text({ url, host }: { url: string, host: string }) {
  return `Sign in to ${host}\n${url}\n\n`
}