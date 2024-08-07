/* tslint:disable */
/* eslint-disable */
/**
 * Core Publishing Service
 * The API for the core publishing service of the Cardano Blockchain announcement and notification application \"Reach Your People\"
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
 * @interface Announcement
 */
export interface Announcement {
    /**
     * 
     * @type {string}
     * @memberof Announcement
     */
    'id': string;
    /**
     * 
     * @type {number}
     * @memberof Announcement
     */
    'projectId': number;
    /**
     * 
     * @type {AnnouncementAnnouncement}
     * @memberof Announcement
     */
    'announcement': AnnouncementAnnouncement;
    /**
     * 
     * @type {string}
     * @memberof Announcement
     */
    'status'?: AnnouncementStatusEnum;
    /**
     * 
     * @type {string}
     * @memberof Announcement
     */
    'shortLink'?: string;
    /**
     * 
     * @type {Audience}
     * @memberof Announcement
     */
    'audience'?: Audience;
    /**
     * 
     * @type {Statistics}
     * @memberof Announcement
     */
    'statistics'?: Statistics;
    /**
     * 
     * @type {string}
     * @memberof Announcement
     */
    'createdDate'?: string;
    /**
     * 
     * @type {string}
     * @memberof Announcement
     */
    'modifiedDate'?: string;
}

export const AnnouncementStatusEnum = {
    Prepared: 'PREPARED',
    Pending: 'PENDING',
    Publishing: 'PUBLISHING',
    Published: 'PUBLISHED',
    Cancelled: 'CANCELLED'
} as const;

export type AnnouncementStatusEnum = typeof AnnouncementStatusEnum[keyof typeof AnnouncementStatusEnum];

/**
 * 
 * @export
 * @interface AnnouncementAnnouncement
 */
export interface AnnouncementAnnouncement {
    /**
     * 
     * @type {string}
     * @memberof AnnouncementAnnouncement
     */
    '@context': string;
    /**
     * 
     * @type {string}
     * @memberof AnnouncementAnnouncement
     */
    'type': AnnouncementAnnouncementTypeEnum;
    /**
     * 
     * @type {PublishAnnouncementForProjectRequestActor}
     * @memberof AnnouncementAnnouncement
     */
    'actor': PublishAnnouncementForProjectRequestActor;
    /**
     * 
     * @type {string}
     * @memberof AnnouncementAnnouncement
     */
    'content': string;
    /**
     * 
     * @type {Array<string>}
     * @memberof AnnouncementAnnouncement
     */
    'to'?: Array<string>;
    /**
     * 
     * @type {string}
     * @memberof AnnouncementAnnouncement
     */
    'published'?: string;
    /**
     * 
     * @type {string}
     * @memberof AnnouncementAnnouncement
     */
    'summary'?: string;
}

export const AnnouncementAnnouncementTypeEnum = {
    Announce: 'Announce'
} as const;

export type AnnouncementAnnouncementTypeEnum = typeof AnnouncementAnnouncementTypeEnum[keyof typeof AnnouncementAnnouncementTypeEnum];

/**
 * 
 * @export
 * @interface Audience
 */
export interface Audience {
    /**
     * If for a token-based project, the list of policy IDs this announcement was published to.
     * @type {Array<string>}
     * @memberof Audience
     */
    'policies'?: Array<string>;
}
/**
 * 
 * @export
 * @interface BasicAnnouncement
 */
export interface BasicAnnouncement {
    /**
     * 
     * @type {string}
     * @memberof BasicAnnouncement
     */
    'id'?: string;
    /**
     * The subscription service account ID of the user account submitting the announcement
     * @type {number}
     * @memberof BasicAnnouncement
     */
    'author': number;
    /**
     * 
     * @type {string}
     * @memberof BasicAnnouncement
     */
    'title': string;
    /**
     * 
     * @type {string}
     * @memberof BasicAnnouncement
     */
    'content': string;
    /**
     * 
     * @type {string}
     * @memberof BasicAnnouncement
     */
    'externalLink'?: string;
    /**
     * If for a token-based project, the list of policy IDs to publish to.
     * @type {Array<string>}
     * @memberof BasicAnnouncement
     */
    'policies'?: Array<string>;
    /**
     * 
     * @type {string}
     * @memberof BasicAnnouncement
     */
    'type'?: BasicAnnouncementTypeEnum;
}

export const BasicAnnouncementTypeEnum = {
    Standard: 'STANDARD',
    Test: 'TEST'
} as const;

export type BasicAnnouncementTypeEnum = typeof BasicAnnouncementTypeEnum[keyof typeof BasicAnnouncementTypeEnum];

/**
 * 
 * @export
 * @interface PolicyPublishingPermission
 */
export interface PolicyPublishingPermission {
    /**
     * The Policy ID
     * @type {string}
     * @memberof PolicyPublishingPermission
     */
    'policyId': string;
    /**
     * 
     * @type {string}
     * @memberof PolicyPublishingPermission
     */
    'permission': PolicyPublishingPermissionPermissionEnum;
}

export const PolicyPublishingPermissionPermissionEnum = {
    Manual: 'PUBLISHING_MANUAL',
    Cip66: 'PUBLISHING_CIP66'
} as const;

export type PolicyPublishingPermissionPermissionEnum = typeof PolicyPublishingPermissionPermissionEnum[keyof typeof PolicyPublishingPermissionPermissionEnum];

/**
 * 
 * @export
 * @interface PublishAnnouncementForProjectRequest
 */
export interface PublishAnnouncementForProjectRequest {
    /**
     * 
     * @type {string}
     * @memberof PublishAnnouncementForProjectRequest
     */
    '@context': string;
    /**
     * 
     * @type {string}
     * @memberof PublishAnnouncementForProjectRequest
     */
    'type': PublishAnnouncementForProjectRequestTypeEnum;
    /**
     * 
     * @type {PublishAnnouncementForProjectRequestActor}
     * @memberof PublishAnnouncementForProjectRequest
     */
    'actor': PublishAnnouncementForProjectRequestActor;
    /**
     * 
     * @type {string}
     * @memberof PublishAnnouncementForProjectRequest
     */
    'content': string;
    /**
     * 
     * @type {Array<string>}
     * @memberof PublishAnnouncementForProjectRequest
     */
    'to'?: Array<string>;
    /**
     * 
     * @type {string}
     * @memberof PublishAnnouncementForProjectRequest
     */
    'published'?: string;
    /**
     * 
     * @type {string}
     * @memberof PublishAnnouncementForProjectRequest
     */
    'summary'?: string;
}

export const PublishAnnouncementForProjectRequestTypeEnum = {
    Announce: 'Announce'
} as const;

export type PublishAnnouncementForProjectRequestTypeEnum = typeof PublishAnnouncementForProjectRequestTypeEnum[keyof typeof PublishAnnouncementForProjectRequestTypeEnum];

/**
 * 
 * @export
 * @interface PublishAnnouncementForProjectRequestActor
 */
export interface PublishAnnouncementForProjectRequestActor {
    /**
     * 
     * @type {string}
     * @memberof PublishAnnouncementForProjectRequestActor
     */
    'type': PublishAnnouncementForProjectRequestActorTypeEnum;
    /**
     * 
     * @type {string}
     * @memberof PublishAnnouncementForProjectRequestActor
     */
    'id': string;
}

export const PublishAnnouncementForProjectRequestActorTypeEnum = {
    Application: 'Application',
    Group: 'Group',
    Organization: 'Organization',
    Person: 'Person',
    Service: 'Service'
} as const;

export type PublishAnnouncementForProjectRequestActorTypeEnum = typeof PublishAnnouncementForProjectRequestActorTypeEnum[keyof typeof PublishAnnouncementForProjectRequestActorTypeEnum];

/**
 * 
 * @export
 * @interface PublishingPermissions
 */
export interface PublishingPermissions {
    /**
     * 
     * @type {Array<PolicyPublishingPermission>}
     * @memberof PublishingPermissions
     */
    'policies': Array<PolicyPublishingPermission>;
    /**
     * 
     * @type {number}
     * @memberof PublishingPermissions
     */
    'accountId': number;
}
/**
 * 
 * @export
 * @interface Statistics
 */
export interface Statistics {
    /**
     * 
     * @type {{ [key: string]: number; }}
     * @memberof Statistics
     */
    'sent'?: { [key: string]: number; };
    /**
     * 
     * @type {number}
     * @memberof Statistics
     */
    'uniqueAccounts'?: number;
    /**
     * 
     * @type {number}
     * @memberof Statistics
     */
    'explicitSubscribers'?: number;
    /**
     * 
     * @type {{ [key: string]: number; }}
     * @memberof Statistics
     */
    'delivered'?: { [key: string]: number; };
    /**
     * 
     * @type {{ [key: string]: number; }}
     * @memberof Statistics
     */
    'failures'?: { [key: string]: number; };
    /**
     * 
     * @type {{ [key: string]: number; }}
     * @memberof Statistics
     */
    'views'?: { [key: string]: number; };
}

/**
 * DefaultApi - axios parameter creator
 * @export
 */
export const DefaultApiAxiosParamCreator = function (configuration?: Configuration) {
    return {
        /**
         * Get an announcement and its details and status by announcement UUID
         * @summary Get announcement by ID
         * @param {string} announcementId The UUID of an announcement
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getAnnouncementById: async (announcementId: string, options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            // verify required parameter 'announcementId' is not null or undefined
            assertParamExists('getAnnouncementById', 'announcementId', announcementId)
            const localVarPath = `/announcements/{announcementId}`
                .replace(`{${"announcementId"}}`, encodeURIComponent(String(announcementId)));
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
         * Get the roles and permissions to publishing rights for a project and the related policies and assets.
         * @summary Get the publishing role status
         * @param {number} projectId The numeric ID of a Project
         * @param {number} accountId The numeric ID of an account
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getPublishingPermissionsForAccount: async (projectId: number, accountId: number, options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            // verify required parameter 'projectId' is not null or undefined
            assertParamExists('getPublishingPermissionsForAccount', 'projectId', projectId)
            // verify required parameter 'accountId' is not null or undefined
            assertParamExists('getPublishingPermissionsForAccount', 'accountId', accountId)
            const localVarPath = `/projects/{projectId}/roles/{accountId}`
                .replace(`{${"projectId"}}`, encodeURIComponent(String(projectId)))
                .replace(`{${"accountId"}}`, encodeURIComponent(String(accountId)));
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
         * List all announcements that a specific project has published, regardless of target.
         * @summary List announcements for a specific project
         * @param {number} projectId The numeric ID of a Project
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        listAnnouncementsForProject: async (projectId: number, options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            // verify required parameter 'projectId' is not null or undefined
            assertParamExists('listAnnouncementsForProject', 'projectId', projectId)
            const localVarPath = `/projects/{projectId}/announcements`
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
         * Publish an announcement for a project, token policy, SPO, dRep or social media account to all verified holders that have subscribed to updates, without exposing any social media or messaging identifiers to the publisher.
         * @summary Publish new announcement for a specific project
         * @param {number} projectId The numeric ID of a Project
         * @param {PublishAnnouncementForProjectRequest} publishAnnouncementForProjectRequest Can be either a JSON object with the required data or the raw Activity Streams 2.0 Announcement JSON body
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        publishAnnouncementForProject: async (projectId: number, publishAnnouncementForProjectRequest: PublishAnnouncementForProjectRequest, options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            // verify required parameter 'projectId' is not null or undefined
            assertParamExists('publishAnnouncementForProject', 'projectId', projectId)
            // verify required parameter 'publishAnnouncementForProjectRequest' is not null or undefined
            assertParamExists('publishAnnouncementForProject', 'publishAnnouncementForProjectRequest', publishAnnouncementForProjectRequest)
            const localVarPath = `/projects/{projectId}/announcements`
                .replace(`{${"projectId"}}`, encodeURIComponent(String(projectId)));
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'POST', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;


    
            localVarHeaderParameter['Content-Type'] = 'application/activity+json';

            setSearchParams(localVarUrlObj, localVarQueryParameter);
            let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};
            localVarRequestOptions.data = serializeDataIfNeeded(publishAnnouncementForProjectRequest, localVarRequestOptions, configuration)

            return {
                url: toPathString(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * 
         * @summary Send test announcement to account
         * @param {number} accountId The numeric ID of an account
         * @param {number} externalAccountId The numeric ID of an external account
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        sendTestAnnouncement: async (accountId: number, externalAccountId: number, options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            // verify required parameter 'accountId' is not null or undefined
            assertParamExists('sendTestAnnouncement', 'accountId', accountId)
            // verify required parameter 'externalAccountId' is not null or undefined
            assertParamExists('sendTestAnnouncement', 'externalAccountId', externalAccountId)
            const localVarPath = `/accounts/{accountId}/externalaccounts/{externalAccountId}/test`
                .replace(`{${"accountId"}}`, encodeURIComponent(String(accountId)))
                .replace(`{${"externalAccountId"}}`, encodeURIComponent(String(externalAccountId)));
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'POST', ...baseOptions, ...options};
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
         * Get an announcement and its details and status by announcement UUID
         * @summary Get announcement by ID
         * @param {string} announcementId The UUID of an announcement
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async getAnnouncementById(announcementId: string, options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<Announcement>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.getAnnouncementById(announcementId, options);
            const localVarOperationServerIndex = configuration?.serverIndex ?? 0;
            const localVarOperationServerBasePath = operationServerMap['DefaultApi.getAnnouncementById']?.[localVarOperationServerIndex]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, localVarOperationServerBasePath || basePath);
        },
        /**
         * Get the roles and permissions to publishing rights for a project and the related policies and assets.
         * @summary Get the publishing role status
         * @param {number} projectId The numeric ID of a Project
         * @param {number} accountId The numeric ID of an account
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async getPublishingPermissionsForAccount(projectId: number, accountId: number, options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<PublishingPermissions>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.getPublishingPermissionsForAccount(projectId, accountId, options);
            const localVarOperationServerIndex = configuration?.serverIndex ?? 0;
            const localVarOperationServerBasePath = operationServerMap['DefaultApi.getPublishingPermissionsForAccount']?.[localVarOperationServerIndex]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, localVarOperationServerBasePath || basePath);
        },
        /**
         * List all announcements that a specific project has published, regardless of target.
         * @summary List announcements for a specific project
         * @param {number} projectId The numeric ID of a Project
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async listAnnouncementsForProject(projectId: number, options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<Array<Announcement>>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.listAnnouncementsForProject(projectId, options);
            const localVarOperationServerIndex = configuration?.serverIndex ?? 0;
            const localVarOperationServerBasePath = operationServerMap['DefaultApi.listAnnouncementsForProject']?.[localVarOperationServerIndex]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, localVarOperationServerBasePath || basePath);
        },
        /**
         * Publish an announcement for a project, token policy, SPO, dRep or social media account to all verified holders that have subscribed to updates, without exposing any social media or messaging identifiers to the publisher.
         * @summary Publish new announcement for a specific project
         * @param {number} projectId The numeric ID of a Project
         * @param {PublishAnnouncementForProjectRequest} publishAnnouncementForProjectRequest Can be either a JSON object with the required data or the raw Activity Streams 2.0 Announcement JSON body
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async publishAnnouncementForProject(projectId: number, publishAnnouncementForProjectRequest: PublishAnnouncementForProjectRequest, options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<void>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.publishAnnouncementForProject(projectId, publishAnnouncementForProjectRequest, options);
            const localVarOperationServerIndex = configuration?.serverIndex ?? 0;
            const localVarOperationServerBasePath = operationServerMap['DefaultApi.publishAnnouncementForProject']?.[localVarOperationServerIndex]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, localVarOperationServerBasePath || basePath);
        },
        /**
         * 
         * @summary Send test announcement to account
         * @param {number} accountId The numeric ID of an account
         * @param {number} externalAccountId The numeric ID of an external account
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async sendTestAnnouncement(accountId: number, externalAccountId: number, options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<BasicAnnouncement>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.sendTestAnnouncement(accountId, externalAccountId, options);
            const localVarOperationServerIndex = configuration?.serverIndex ?? 0;
            const localVarOperationServerBasePath = operationServerMap['DefaultApi.sendTestAnnouncement']?.[localVarOperationServerIndex]?.url;
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
         * Get an announcement and its details and status by announcement UUID
         * @summary Get announcement by ID
         * @param {string} announcementId The UUID of an announcement
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getAnnouncementById(announcementId: string, options?: any): AxiosPromise<Announcement> {
            return localVarFp.getAnnouncementById(announcementId, options).then((request) => request(axios, basePath));
        },
        /**
         * Get the roles and permissions to publishing rights for a project and the related policies and assets.
         * @summary Get the publishing role status
         * @param {number} projectId The numeric ID of a Project
         * @param {number} accountId The numeric ID of an account
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getPublishingPermissionsForAccount(projectId: number, accountId: number, options?: any): AxiosPromise<PublishingPermissions> {
            return localVarFp.getPublishingPermissionsForAccount(projectId, accountId, options).then((request) => request(axios, basePath));
        },
        /**
         * List all announcements that a specific project has published, regardless of target.
         * @summary List announcements for a specific project
         * @param {number} projectId The numeric ID of a Project
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        listAnnouncementsForProject(projectId: number, options?: any): AxiosPromise<Array<Announcement>> {
            return localVarFp.listAnnouncementsForProject(projectId, options).then((request) => request(axios, basePath));
        },
        /**
         * Publish an announcement for a project, token policy, SPO, dRep or social media account to all verified holders that have subscribed to updates, without exposing any social media or messaging identifiers to the publisher.
         * @summary Publish new announcement for a specific project
         * @param {number} projectId The numeric ID of a Project
         * @param {PublishAnnouncementForProjectRequest} publishAnnouncementForProjectRequest Can be either a JSON object with the required data or the raw Activity Streams 2.0 Announcement JSON body
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        publishAnnouncementForProject(projectId: number, publishAnnouncementForProjectRequest: PublishAnnouncementForProjectRequest, options?: any): AxiosPromise<void> {
            return localVarFp.publishAnnouncementForProject(projectId, publishAnnouncementForProjectRequest, options).then((request) => request(axios, basePath));
        },
        /**
         * 
         * @summary Send test announcement to account
         * @param {number} accountId The numeric ID of an account
         * @param {number} externalAccountId The numeric ID of an external account
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        sendTestAnnouncement(accountId: number, externalAccountId: number, options?: any): AxiosPromise<BasicAnnouncement> {
            return localVarFp.sendTestAnnouncement(accountId, externalAccountId, options).then((request) => request(axios, basePath));
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
     * Get an announcement and its details and status by announcement UUID
     * @summary Get announcement by ID
     * @param {string} announcementId The UUID of an announcement
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof DefaultApi
     */
    public getAnnouncementById(announcementId: string, options?: RawAxiosRequestConfig) {
        return DefaultApiFp(this.configuration).getAnnouncementById(announcementId, options).then((request) => request(this.axios, this.basePath));
    }

    /**
     * Get the roles and permissions to publishing rights for a project and the related policies and assets.
     * @summary Get the publishing role status
     * @param {number} projectId The numeric ID of a Project
     * @param {number} accountId The numeric ID of an account
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof DefaultApi
     */
    public getPublishingPermissionsForAccount(projectId: number, accountId: number, options?: RawAxiosRequestConfig) {
        return DefaultApiFp(this.configuration).getPublishingPermissionsForAccount(projectId, accountId, options).then((request) => request(this.axios, this.basePath));
    }

    /**
     * List all announcements that a specific project has published, regardless of target.
     * @summary List announcements for a specific project
     * @param {number} projectId The numeric ID of a Project
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof DefaultApi
     */
    public listAnnouncementsForProject(projectId: number, options?: RawAxiosRequestConfig) {
        return DefaultApiFp(this.configuration).listAnnouncementsForProject(projectId, options).then((request) => request(this.axios, this.basePath));
    }

    /**
     * Publish an announcement for a project, token policy, SPO, dRep or social media account to all verified holders that have subscribed to updates, without exposing any social media or messaging identifiers to the publisher.
     * @summary Publish new announcement for a specific project
     * @param {number} projectId The numeric ID of a Project
     * @param {PublishAnnouncementForProjectRequest} publishAnnouncementForProjectRequest Can be either a JSON object with the required data or the raw Activity Streams 2.0 Announcement JSON body
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof DefaultApi
     */
    public publishAnnouncementForProject(projectId: number, publishAnnouncementForProjectRequest: PublishAnnouncementForProjectRequest, options?: RawAxiosRequestConfig) {
        return DefaultApiFp(this.configuration).publishAnnouncementForProject(projectId, publishAnnouncementForProjectRequest, options).then((request) => request(this.axios, this.basePath));
    }

    /**
     * 
     * @summary Send test announcement to account
     * @param {number} accountId The numeric ID of an account
     * @param {number} externalAccountId The numeric ID of an external account
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof DefaultApi
     */
    public sendTestAnnouncement(accountId: number, externalAccountId: number, options?: RawAxiosRequestConfig) {
        return DefaultApiFp(this.configuration).sendTestAnnouncement(accountId, externalAccountId, options).then((request) => request(this.axios, this.basePath));
    }
}



