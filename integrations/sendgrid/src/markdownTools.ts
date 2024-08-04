import { marked } from 'marked';
import type { Token, Tokens, TokensList } from 'marked';
import { JSDOM } from 'jsdom';
import DOMPurify from 'dompurify';

export function stripMarkdown(markdown: string): string {
  const tokens = marked.lexer(markdown);

  const processTokens = (tokens: TokensList | Token[]): string => {
    let result = '';

    tokens.forEach((token: Token) => {
      switch (token.type) {
        case 'text':
          result += token.text;
          break;
        case 'list':
          result += processList(token);
          break;
        case 'list_item':
          result += '- ' + processTokens(token.tokens!);
          break;
        case 'heading':
          result += processTokens(token.tokens!) + '\n';
          break;
        case 'paragraph':
          result += '\n' + processTokens(token.tokens!) + '\n';
          break;
        case 'space':
          result += '\n';
          break;
        case 'image':
          result += `[${token.text}](${token.href})`;
          break;
        default:
          if ('tokens' in token && token.tokens) {
            result += processTokens(token.tokens);
          }
          break;
      }
    });

    return result;
  };

  const processList = (listToken: Tokens.List | Tokens.Generic): string => {
    let listText = '';
    let itemIndex = 1;
    listToken.items.forEach((item: any) => {
      listText += (listToken.ordered ? `${itemIndex}. ` : '- ');
      listText += processTokens(item.tokens) + '\n';
      itemIndex += 1;
    });
    return listText.trim();
  };

  return processTokens(tokens).trim();
};


export async function htmlizeMarkdown(markdown: string): Promise<string> {
  const markdownHtml = await marked(markdown);
  const window = new JSDOM('').window;
  const purify = DOMPurify(window);
  return purify.sanitize(markdownHtml, { USE_PROFILES: { html: true } });
}