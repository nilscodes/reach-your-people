/* tslint:disable */
/* eslint-disable */
/**
 * Core Redirect Service
 * The API for the core URL shortener and redirect service of the Cardano Blockchain announcement and notification application \"Reach Your People\"
 *
 * The version of the OpenAPI document: 1.0
 * Contact: contact@vibrantnet.io
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


import type { Configuration } from './configuration';
import type { AxiosPromise, AxiosInstance, RawAxiosRequestConfig } from 'axios';
import globalAxios from 'axios';
// Some imports not used depending on template conditions
// @ts-ignore
import { DUMMY_BASE_URL, assertParamExists, setApiKeyToObject, setBasicAuthToObject, setBearerAuthToObject, setOAuthToObject, setSearchParams, serializeDataIfNeeded, toPathString, createRequestFunction } from './common';
import type { RequestArgs } from './base';
// @ts-ignore
import { BASE_PATH, COLLECTION_FORMATS, BaseAPI, RequiredError, operationServerMap } from './base';

/**
 * 
 * @export
 * @interface ShortenedUrl
 */
export interface ShortenedUrl {
    /**
     * 
     * @type {string}
     * @memberof ShortenedUrl
     */
    'id'?: string;
    /**
     * 
     * @type {string}
     * @memberof ShortenedUrl
     */
    'shortcode'?: string;
    /**
     * 
     * @type {string}
     * @memberof ShortenedUrl
     */
    'type': ShortenedUrlTypeEnum;
    /**
     * 
     * @type {string}
     * @memberof ShortenedUrl
     */
    'createTime'?: string;
    /**
     * 
     * @type {string}
     * @memberof ShortenedUrl
     */
    'status': ShortenedUrlStatusEnum;
    /**
     * 
     * @type {string}
     * @memberof ShortenedUrl
     */
    'url': string;
    /**
     * 
     * @type {number}
     * @memberof ShortenedUrl
     */
    'views'?: number;
    /**
     * 
     * @type {number}
     * @memberof ShortenedUrl
     */
    'projectId'?: number;
}

export const ShortenedUrlTypeEnum = {
    Ryp: 'RYP',
    External: 'EXTERNAL'
} as const;

export type ShortenedUrlTypeEnum = typeof ShortenedUrlTypeEnum[keyof typeof ShortenedUrlTypeEnum];
export const ShortenedUrlStatusEnum = {
    Active: 'ACTIVE',
    Inactive: 'INACTIVE'
} as const;

export type ShortenedUrlStatusEnum = typeof ShortenedUrlStatusEnum[keyof typeof ShortenedUrlStatusEnum];

/**
 * 
 * @export
 * @interface ShortenedUrlPartial
 */
export interface ShortenedUrlPartial {
    /**
     * 
     * @type {string}
     * @memberof ShortenedUrlPartial
     */
    'shortcode'?: string;
    /**
     * 
     * @type {string}
     * @memberof ShortenedUrlPartial
     */
    'status'?: ShortenedUrlPartialStatusEnum;
    /**
     * 
     * @type {string}
     * @memberof ShortenedUrlPartial
     */
    'type'?: ShortenedUrlPartialTypeEnum;
    /**
     * 
     * @type {string}
     * @memberof ShortenedUrlPartial
     */
    'url'?: string;
}

export const ShortenedUrlPartialStatusEnum = {
    Active: 'ACTIVE',
    Inactive: 'INACTIVE'
} as const;

export type ShortenedUrlPartialStatusEnum = typeof ShortenedUrlPartialStatusEnum[keyof typeof ShortenedUrlPartialStatusEnum];
export const ShortenedUrlPartialTypeEnum = {
    Ryp: 'RYP',
    External: 'EXTERNAL'
} as const;

export type ShortenedUrlPartialTypeEnum = typeof ShortenedUrlPartialTypeEnum[keyof typeof ShortenedUrlPartialTypeEnum];


/**
 * DefaultApi - axios parameter creator
 * @export
 */
export const DefaultApiAxiosParamCreator = function (configuration?: Configuration) {
    return {
        /**
         * Create a new, shortened URL and expose a redirect endpoint for it.
         * @summary Create new and shortened redirect URL
         * @param {ShortenedUrl} [shortenedUrl] 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        createShortUrl: async (shortenedUrl?: ShortenedUrl, options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            const localVarPath = `/urls`;
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'POST', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;


    
            localVarHeaderParameter['Content-Type'] = 'application/json';

            setSearchParams(localVarUrlObj, localVarQueryParameter);
            let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};
            localVarRequestOptions.data = serializeDataIfNeeded(shortenedUrl, localVarRequestOptions, configuration)

            return {
                url: toPathString(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * Get a shortened URL and its details by ID
         * @summary Get URL by ID
         * @param {string} urlId The URL UUID (not the shortcode identifier used for the shortened URL itself)
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getUrlById: async (urlId: string, options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            // verify required parameter 'urlId' is not null or undefined
            assertParamExists('getUrlById', 'urlId', urlId)
            const localVarPath = `/urls/{urlId}`
                .replace(`{${"urlId"}}`, encodeURIComponent(String(urlId)));
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'GET', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;


    
            setSearchParams(localVarUrlObj, localVarQueryParameter);
            let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};

            return {
                url: toPathString(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * Get a shortened URL and its details by shortcode
         * @summary Get URL by shortcode
         * @param {string} shortcode The shortcode of the URL to look up
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getUrlByShortcode: async (shortcode: string, options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            // verify required parameter 'shortcode' is not null or undefined
            assertParamExists('getUrlByShortcode', 'shortcode', shortcode)
            const localVarPath = `/urls/shortcode/{shortcode}`
                .replace(`{${"shortcode"}}`, encodeURIComponent(String(shortcode)));
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'GET', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;


    
            setSearchParams(localVarUrlObj, localVarQueryParameter);
            let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};

            return {
                url: toPathString(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * Retrieve all active and inactive URLs for a given project
         * @summary Get all URLs for a project
         * @param {number} projectId The numeric ID of a Project
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getUrlsForProject: async (projectId: number, options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            // verify required parameter 'projectId' is not null or undefined
            assertParamExists('getUrlsForProject', 'projectId', projectId)
            const localVarPath = `/urls/projects/{projectId}`
                .replace(`{${"projectId"}}`, encodeURIComponent(String(projectId)));
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'GET', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;


    
            setSearchParams(localVarUrlObj, localVarQueryParameter);
            let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};

            return {
                url: toPathString(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * Redirects to the URL with the given shortcode if it is active, and updates any associated statistics.
         * @summary Redirect to URL
         * @param {string} shortcode The shortcode of the URL to look up
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        redirectToUrl: async (shortcode: string, options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            // verify required parameter 'shortcode' is not null or undefined
            assertParamExists('redirectToUrl', 'shortcode', shortcode)
            const localVarPath = `/redirect/{shortcode}`
                .replace(`{${"shortcode"}}`, encodeURIComponent(String(shortcode)));
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'GET', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;


    
            setSearchParams(localVarUrlObj, localVarQueryParameter);
            let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};

            return {
                url: toPathString(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * Update the details of a redirect URL by providing the new details and existing URL id
         * @summary Update URL details by ID
         * @param {string} urlId The URL UUID (not the shortcode identifier used for the shortened URL itself)
         * @param {ShortenedUrlPartial} [shortenedUrlPartial] 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        updateUrlById: async (urlId: string, shortenedUrlPartial?: ShortenedUrlPartial, options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            // verify required parameter 'urlId' is not null or undefined
            assertParamExists('updateUrlById', 'urlId', urlId)
            const localVarPath = `/urls/{urlId}`
                .replace(`{${"urlId"}}`, encodeURIComponent(String(urlId)));
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'PATCH', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;


    
            localVarHeaderParameter['Content-Type'] = 'application/json';

            setSearchParams(localVarUrlObj, localVarQueryParameter);
            let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};
            localVarRequestOptions.data = serializeDataIfNeeded(shortenedUrlPartial, localVarRequestOptions, configuration)

            return {
                url: toPathString(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
    }
};

/**
 * DefaultApi - functional programming interface
 * @export
 */
export const DefaultApiFp = function(configuration?: Configuration) {
    const localVarAxiosParamCreator = DefaultApiAxiosParamCreator(configuration)
    return {
        /**
         * Create a new, shortened URL and expose a redirect endpoint for it.
         * @summary Create new and shortened redirect URL
         * @param {ShortenedUrl} [shortenedUrl] 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async createShortUrl(shortenedUrl?: ShortenedUrl, options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<ShortenedUrl>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.createShortUrl(shortenedUrl, options);
            const localVarOperationServerIndex = configuration?.serverIndex ?? 0;
            const localVarOperationServerBasePath = operationServerMap['DefaultApi.createShortUrl']?.[localVarOperationServerIndex]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, localVarOperationServerBasePath || basePath);
        },
        /**
         * Get a shortened URL and its details by ID
         * @summary Get URL by ID
         * @param {string} urlId The URL UUID (not the shortcode identifier used for the shortened URL itself)
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async getUrlById(urlId: string, options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<ShortenedUrl>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.getUrlById(urlId, options);
            const localVarOperationServerIndex = configuration?.serverIndex ?? 0;
            const localVarOperationServerBasePath = operationServerMap['DefaultApi.getUrlById']?.[localVarOperationServerIndex]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, localVarOperationServerBasePath || basePath);
        },
        /**
         * Get a shortened URL and its details by shortcode
         * @summary Get URL by shortcode
         * @param {string} shortcode The shortcode of the URL to look up
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async getUrlByShortcode(shortcode: string, options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<ShortenedUrl>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.getUrlByShortcode(shortcode, options);
            const localVarOperationServerIndex = configuration?.serverIndex ?? 0;
            const localVarOperationServerBasePath = operationServerMap['DefaultApi.getUrlByShortcode']?.[localVarOperationServerIndex]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, localVarOperationServerBasePath || basePath);
        },
        /**
         * Retrieve all active and inactive URLs for a given project
         * @summary Get all URLs for a project
         * @param {number} projectId The numeric ID of a Project
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async getUrlsForProject(projectId: number, options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<Array<ShortenedUrl>>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.getUrlsForProject(projectId, options);
            const localVarOperationServerIndex = configuration?.serverIndex ?? 0;
            const localVarOperationServerBasePath = operationServerMap['DefaultApi.getUrlsForProject']?.[localVarOperationServerIndex]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, localVarOperationServerBasePath || basePath);
        },
        /**
         * Redirects to the URL with the given shortcode if it is active, and updates any associated statistics.
         * @summary Redirect to URL
         * @param {string} shortcode The shortcode of the URL to look up
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async redirectToUrl(shortcode: string, options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<void>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.redirectToUrl(shortcode, options);
            const localVarOperationServerIndex = configuration?.serverIndex ?? 0;
            const localVarOperationServerBasePath = operationServerMap['DefaultApi.redirectToUrl']?.[localVarOperationServerIndex]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, localVarOperationServerBasePath || basePath);
        },
        /**
         * Update the details of a redirect URL by providing the new details and existing URL id
         * @summary Update URL details by ID
         * @param {string} urlId The URL UUID (not the shortcode identifier used for the shortened URL itself)
         * @param {ShortenedUrlPartial} [shortenedUrlPartial] 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async updateUrlById(urlId: string, shortenedUrlPartial?: ShortenedUrlPartial, options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<ShortenedUrl>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.updateUrlById(urlId, shortenedUrlPartial, options);
            const localVarOperationServerIndex = configuration?.serverIndex ?? 0;
            const localVarOperationServerBasePath = operationServerMap['DefaultApi.updateUrlById']?.[localVarOperationServerIndex]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, localVarOperationServerBasePath || basePath);
        },
    }
};

/**
 * DefaultApi - factory interface
 * @export
 */
export const DefaultApiFactory = function (configuration?: Configuration, basePath?: string, axios?: AxiosInstance) {
    const localVarFp = DefaultApiFp(configuration)
    return {
        /**
         * Create a new, shortened URL and expose a redirect endpoint for it.
         * @summary Create new and shortened redirect URL
         * @param {ShortenedUrl} [shortenedUrl] 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        createShortUrl(shortenedUrl?: ShortenedUrl, options?: any): AxiosPromise<ShortenedUrl> {
            return localVarFp.createShortUrl(shortenedUrl, options).then((request) => request(axios, basePath));
        },
        /**
         * Get a shortened URL and its details by ID
         * @summary Get URL by ID
         * @param {string} urlId The URL UUID (not the shortcode identifier used for the shortened URL itself)
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getUrlById(urlId: string, options?: any): AxiosPromise<ShortenedUrl> {
            return localVarFp.getUrlById(urlId, options).then((request) => request(axios, basePath));
        },
        /**
         * Get a shortened URL and its details by shortcode
         * @summary Get URL by shortcode
         * @param {string} shortcode The shortcode of the URL to look up
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getUrlByShortcode(shortcode: string, options?: any): AxiosPromise<ShortenedUrl> {
            return localVarFp.getUrlByShortcode(shortcode, options).then((request) => request(axios, basePath));
        },
        /**
         * Retrieve all active and inactive URLs for a given project
         * @summary Get all URLs for a project
         * @param {number} projectId The numeric ID of a Project
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getUrlsForProject(projectId: number, options?: any): AxiosPromise<Array<ShortenedUrl>> {
            return localVarFp.getUrlsForProject(projectId, options).then((request) => request(axios, basePath));
        },
        /**
         * Redirects to the URL with the given shortcode if it is active, and updates any associated statistics.
         * @summary Redirect to URL
         * @param {string} shortcode The shortcode of the URL to look up
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        redirectToUrl(shortcode: string, options?: any): AxiosPromise<void> {
            return localVarFp.redirectToUrl(shortcode, options).then((request) => request(axios, basePath));
        },
        /**
         * Update the details of a redirect URL by providing the new details and existing URL id
         * @summary Update URL details by ID
         * @param {string} urlId The URL UUID (not the shortcode identifier used for the shortened URL itself)
         * @param {ShortenedUrlPartial} [shortenedUrlPartial] 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        updateUrlById(urlId: string, shortenedUrlPartial?: ShortenedUrlPartial, options?: any): AxiosPromise<ShortenedUrl> {
            return localVarFp.updateUrlById(urlId, shortenedUrlPartial, options).then((request) => request(axios, basePath));
        },
    };
};

/**
 * DefaultApi - object-oriented interface
 * @export
 * @class DefaultApi
 * @extends {BaseAPI}
 */
export class DefaultApi extends BaseAPI {
    /**
     * Create a new, shortened URL and expose a redirect endpoint for it.
     * @summary Create new and shortened redirect URL
     * @param {ShortenedUrl} [shortenedUrl] 
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof DefaultApi
     */
    public createShortUrl(shortenedUrl?: ShortenedUrl, options?: RawAxiosRequestConfig) {
        return DefaultApiFp(this.configuration).createShortUrl(shortenedUrl, options).then((request) => request(this.axios, this.basePath));
    }

    /**
     * Get a shortened URL and its details by ID
     * @summary Get URL by ID
     * @param {string} urlId The URL UUID (not the shortcode identifier used for the shortened URL itself)
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof DefaultApi
     */
    public getUrlById(urlId: string, options?: RawAxiosRequestConfig) {
        return DefaultApiFp(this.configuration).getUrlById(urlId, options).then((request) => request(this.axios, this.basePath));
    }

    /**
     * Get a shortened URL and its details by shortcode
     * @summary Get URL by shortcode
     * @param {string} shortcode The shortcode of the URL to look up
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof DefaultApi
     */
    public getUrlByShortcode(shortcode: string, options?: RawAxiosRequestConfig) {
        return DefaultApiFp(this.configuration).getUrlByShortcode(shortcode, options).then((request) => request(this.axios, this.basePath));
    }

    /**
     * Retrieve all active and inactive URLs for a given project
     * @summary Get all URLs for a project
     * @param {number} projectId The numeric ID of a Project
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof DefaultApi
     */
    public getUrlsForProject(projectId: number, options?: RawAxiosRequestConfig) {
        return DefaultApiFp(this.configuration).getUrlsForProject(projectId, options).then((request) => request(this.axios, this.basePath));
    }

    /**
     * Redirects to the URL with the given shortcode if it is active, and updates any associated statistics.
     * @summary Redirect to URL
     * @param {string} shortcode The shortcode of the URL to look up
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof DefaultApi
     */
    public redirectToUrl(shortcode: string, options?: RawAxiosRequestConfig) {
        return DefaultApiFp(this.configuration).redirectToUrl(shortcode, options).then((request) => request(this.axios, this.basePath));
    }

    /**
     * Update the details of a redirect URL by providing the new details and existing URL id
     * @summary Update URL details by ID
     * @param {string} urlId The URL UUID (not the shortcode identifier used for the shortened URL itself)
     * @param {ShortenedUrlPartial} [shortenedUrlPartial] 
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof DefaultApi
     */
    public updateUrlById(urlId: string, shortenedUrlPartial?: ShortenedUrlPartial, options?: RawAxiosRequestConfig) {
        return DefaultApiFp(this.configuration).updateUrlById(urlId, shortenedUrlPartial, options).then((request) => request(this.axios, this.basePath));
    }
}



