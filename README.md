# iPint-Doc
* [Introduction](#introduction_)
* [How it works?](#how_it_works)
* [Development](#development_section)
  * [Integrating with iPint](#integrating_with_ipint)
  * [Checkout Page](#checkout_page)
  * [API Reference](#api_reference)
    * [/checkout](#checkout_endpoint)
    * [/invoice](#invoice_endpoint)
    * [Example code for authenticated endpoints](#example_code_for_authentcated_endpoints)
      * [Javascript](#javascript_code)
      * [Python](#python_code)

## <a name="introduction_">Introduction</a>
Wecolme to iPint! For merchants and businesses accepting payments is limited to 'fiat currencies' through traditional means. Traditional method are costly, inefficient and risk prone. They are geographically restrictive.

iPint allows businesses to accept crypto payments by its secure, easy to use and easy to integrate payment service.

## <a name="how_it_works">How it works?</a>
### Step 1
Customer selects cryptocurrency as payment method.
### Step 2
Customer selects specific coin & amount of USD to be deposited. QR code/invoice generated with amount & address. Transfers crypto within time limit.
### Step 3
Payment is confirmed on blockchain. Customer gets deposit in his account. Merchant is informed in realtime. Merchants settlement happens in USDT.

Check <a href="https://ipint.io/demo-checkout/" target="_blank">Demo</a>

## <a name="development_section">Development</a>
### <a name="integrating_with_ipint">Integrating with iPint</a>
  * #### iPint API
     Fully customizable integration into any online shop or website
  * #### Pop-in iFrame
     Displaying within your web page
  * #### Redirect to iPint
     Simple to implement, open in iPint secure page
### <a name="checkout_page">Checkout Page</a>
To open iPint checkout page, redirect to https:ipint.io/checkout?id=fetch-id-from-the-following-step
<br /> To get id to be fetched in the redirect url, call [/checkout](#checkout_endpoint) endpoint.
### <a name="api_reference">API Reference</a>
- All endpoints return either a JSON object or array.
- In case of POST method, request data will be JSON 
- Base URL https://api.ipint.io:8003
##### <a name="checkout_endpoint">Checkout</a>
To get id to be fetched in the redirect url.
* ###### URL
  /checkout
* ###### Method
  POST
* ###### Headers
  apikey
* ###### URL Params
  None
* ###### Data Params
  *Required:* client_email_id, client_preferred_fiat_currency, merchant_website
  
  *Conditional:* merchant_id
  
  *Optional:* amount
  
```
{
    "client_email_id": "client-email-address-for-unique-reference-id",
    "client_preferred_fiat_currency": "local-currency-code",
    "amount": "amount-in-local-currency",
    "merchant_id": "ipint-merchant-id",
    "merchant_website": "merchant-website"
}
```
* ###### Success Response
  *Code*: 200
  
  *Content*: {"session_id":"id-to-be-fetched-in-the-url-to-redirect-to-ipint"}
  
  *Note*: Use this id <br />1.To redirect to the iPint checkout page.<br />2. To get status after the payment (call /invoice endpoint)
* ###### Error Response
  *Code*: 400
  *Content*: {"error": true, "message": "error-messsage"}

curl sample code


    curl -H "Content-Type: application/json" -H "apikey: your-api-key" -X POST -d '{"client_email_id":"customer-email-id","client_preferred_fiat_currency":"local-currency-code", "merchant_id": "merchant-id"}' https://api.ipint.io:8003/checkout

`

##### <a name="invoice_endpoint">Invoice</a>
To get payment status and details. Use the id that you got from the [/checkout](#checkout_endpoint) endpoint.
* ###### URL
  /invoice
* ###### Method
  GET
* ###### Headers
  content-type: application/json
  
  apikey: your-api-key
  
  signature: [hmac-signature-using-your-api-secret](#example_code_for_authentcated_endpoints)
  
  nonce: current-unix-time
* ###### URL Params
  id : the id you got from [/checkout](#checkout_endpoint) endpoint
* ###### Data Params
  None
  
curl sample code
```
curl --location --request GET 'https://api.ipint.io:8003/invoice?id=id-from-the-response-of-onboard-endpoint' \
--header 'content-type: application/json' \
--header 'apikey: your-api-key' \
--header 'signature: hmac-signature-using-your-api-secret' \
--header 'nonce: current-unix-time' 
```

#### <a name="example_code_for_authentcated_endpoints">Example code for authenticated endpoints</a>
##### <a name="javascript_code">Javascript</a>
```
const CryptoJS = require('crypto-js') // Standard JavaScript cryptography library
const request = require('request')

const apiKey = 'paste key here'
const apiSecret = 'paste secret here'

const apiPath = '/say' // Example path
//const apiPath = '/say?id=123' // Example path with query params

const nonce = (Date.now() * 1000).toString() // Standard nonce generator. Timestamp * 1000

const body = {} // Field you may change depending on endpoint

let signature = '/api/' + nonce + apiPath  // GET method
// let signature = '/api/' + nonce + apiPath + JSON.stringify(body)  // POST method
// Consists of the word api as it is and  then nonce, api path and request body
// if api method is GET, also add query params along with the api path if any and do not add body
console.log(signature)

const sig = CryptoJS.HmacSHA384(signature, apiSecret).toString()
// The authentication signature is hashed using the private key
console.log(sig)


const options = {
url: 'https://api.ipint.io:8003'+apiPath,
headers: { 'nonce': nonce, 'apikey': apiKey, 'signature': sig }, body: body, json: true }
console.log(options)

// to call GET method
request(options, function (error, response, body) {
// Logs the error if one occurred
console.error('error:', error); 

// Logs the response status code if a response was received
console.log('statusCode:', response && response.statusCode); 

// Logs the response body
console.log('body:', body); 

});

//  // to call POST method
//  request.post(options, (error, response, body) => { //  // Logs the error if one occurred
//  console.error('error:', error);

//  // Logs the response status code if a response was received
//  console.log('statusCode:', response && response.statusCode);
//  // Logs the response body
//  console.log(body);
//})
```

##### <a name="python_code">Python</a>
```
import hashlib
import hmac
import json
import requests
import time
BASE_URL = "https://api.ipint.io:8003"  # mainnet
# BASE_URL = "https://api.ipint.io:8002"  # testnet
API_KEY = "your ipint api key"
API_SECRET = "your ipint api secret"
NONCE = str(int(time.time()))


class TestiPint:
    def __init__(self, api_key=API_KEY, api_secret=API_SECRET, base_url=BASE_URL, nonce=NONCE):
        self.apikey = api_key
        self.apisecret = api_secret
        self.nonce = nonce
        self.baseurl = base_url
        
    def hmacsha384(self, message):
        inp = message.encode()
        secret = self.apisecret.encode()
        digest_maker = hmac.new(secret, inp, hashlib.sha384)
        res = digest_maker.hexdigest()
        return res
        
    def get_headers(self, api_path, body=None):
        print("\nfunction get_header received params: ")
        print("\napi_path: ", api_path)
        print("\nbody: ", body)
        nonce = self.nonce
        if body:
            signature = "/api/{}{}{}".format(nonce, api_path, json.dumps(body))
        else:
            signature = "/api/{}{}".format(nonce, api_path)
        print("\nsignature ", signature)
        sig = self.hmacsha384(signature)
        headers = {
        'nonce': nonce,
        'apikey': self.apikey,
        'signature': sig,
        'content-type': "application/json"
        }
        return headers
        
    def ping(self):
        api_path = '/say'
        url = self.baseurl + api_path
        print("\napi url ", url)body = {}
        headers = self.get_headers(api_path, body)
        res = requests.get(url, headers=headers)
        print(res.status_code)
        print(res.text)
        print(res.json())
        
    def onboard_client(self, client_email_id, client_preferred_fiat_currency='INR'):
        api_path = '/onboard'
        url = self.baseurl + api_path
        print("\napi url ", url)
        body = {
        "client_email_id": client_email_id,
        "client_preferred_fiat_currency": client_preferred_fiat_currency
        }
        headers = self.get_headers(api_path, body)
        json_body = json.dumps(body)
        res = requests.post(url=url, data=json_body, headers=headers)
        print(res.status_code)
        print(res.text)
        print(res.json())
        
    def get_invoice(self, invoice_id, hist=False):
        api_path = '/invoice?id={}'.format(invoice_id)
        if hist:
        api_path += '&hist=true'
        url = self.baseurl + api_path
        body = {}
        headers = self.get_headers(api_path, body)
        res = requests.get(url, headers=headers)
        print(res.status_code)
        print(res.text)
        print(res.json())
        return res.json()
```
