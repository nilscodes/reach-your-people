import { marked } from 'marked';
import type { Token, Tokens, TokensList } from 'marked';

export function stripMarkdown(markdown: string): string {
  const tokens = marked.lexer(markdown);

  const processTokens = (tokens: TokensList | Token[]): string => {
    let result = '';

    tokens.forEach((token: Token) => {
      switch (token.type) {
        case 'text':
          result += token.text.replace(/\./gm, '\\.');
          break;
        case 'list':
          result += processList(token);
          break;
        case 'list_item':
          result += '- ' + processTokens(token.tokens!).replace(/^- /gm, '\\- ');
          break;
        case 'heading':
          result += `*${processTokens(token.tokens!)}*\n`;
          break;
        case 'paragraph':
          result += '\n' + processTokens(token.tokens!) + '\n';
          break;
        case 'space':
          result += '\n';
          break;
        case 'image':
          result += `[${token.text.replace(/\./gm, '\\.')}](${token.href})`;
          break;
        case 'link':
          result += `[${token.text.replace(/\./gm, '\\.')}](${token.href})`;
          break;
        case 'strong':
          result += `**${processTokens(token.tokens!)}**`;
          break;
        case 'em':
          result += `_${processTokens(token.tokens!)}_`;
          break;
        case 'del':
          result += `~~${processTokens(token.tokens!)}~~`;
          break;
        case 'codespan':
          result += `\`${token.text.replace(/\./gm, '\\.')}\``;
          break;
        case 'underline': // Custom underline handling
          result += `__${processTokens(token.tokens!)}__`;
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
      listText += (listToken.ordered ? `${itemIndex}. ` : '\\- ');
      listText += processTokens(item.tokens) + '\n';
      itemIndex += 1;
    });
    return listText.trim();
  };

  return processTokens(tokens).trim();
};
