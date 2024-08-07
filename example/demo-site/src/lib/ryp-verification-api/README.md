## Reach Your People - Verification API Client

This is a client library for the Reach Your People Verification API. It is a wrapper around the API that provides a more convenient interface for interacting with the API.

### Installation

Simply include the library in your project via `npm install @vibrantnet/ryp-verification-api --save` and then import it into your project.

### Usage

After installing, you can create an instance of the `DefaultApi` class and use it to interact with the API.

```typescript
import { Configuration, DefaultApi } from '@vibrantnet/ryp-verification-api'

const RYP_VERIFICATION_API_URL = 'http://localhost:8070';

const coreVerificationApi = new DefaultApi(new Configuration({
    basePath: RYP_VERIFICATION_API_URL
}));
```

### Contributing

The API client is currently autogenerated via the OpenAPI generator. If you would like to contribute to the client, please contribute your changes to the OpenAPI specification over in the RYP main repository at <https://gitlab.com/vibrantnet/ryp>.