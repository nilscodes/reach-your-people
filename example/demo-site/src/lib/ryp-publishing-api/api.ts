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
 * @interface BasicAnnouncement
 */
export interface BasicAnnouncement {
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
    'link'?: string;
}
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
 * DefaultApi - axios parameter creator
 * @export
 */
export const DefaultApiAxiosParamCreator = function (configuration?: Configuration) {
    return {
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
            const localVarPath = `/announcements/{projectId}`
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
}



