# iPint-Doc
* [Introduction](#introduction_)
* [How it works?](#how_it_works)
* [Get Started](#get_started)
  * [Merchant](#get_started_merchant)
  * [Aggregator](#get_started_aggregator)
* [Development](#development_section)
  * [Integrating with iPint](#integrating_with_ipint)
  * [Checkout Page](#checkout_page)
  * [API Reference](#api_reference)
    * [/checkout](#checkout_endpoint)
    * [/invoice](#invoice_endpoint)
    * [For Aggregator/PSP](#aggregator_api_reference)
      * [Onboard Merchant](#aggregator_merchant_onboarding)
      * [Submit Merchant Docs](#aggregator_merchant_docs)
      * [Provide Merchant Settlement Info](#aggregator_merchant_settlement_info)
    * [Example code for authenticated endpoints](#example_code_for_authentcated_endpoints)
      * [Java](#java_code)
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

## <a name="get_started">Get Started</a>
### <a name="get_started_merchant">For Merchant</a>
#### Step 1
Complete registration process at https://dashboard.ipint.io/agr/merchant-registration.html

Once your account get activated, API Credentials will be shared on the registered email address.
#### Step 2
Check [Integration Options](#integrating_with_ipint)
    
    
### <a name="get_started_aggregator">For Aggregator</a>
#### Step 1
Complete registration process at https://dashboard.ipint.io/agr/registration.html

Once your account get activated, API Credentials will be shared on the registered email address.

#### Step 2
Once PSP account gets verified, PSP can onboard merchant

It requires 
1. merchant’s confirmation that merchant is agree with iPint’s Terms of Service, Privacy Policy and AML/CFT Policy
2. merchant’s confirmation that PSP can share merchant’s kyc details with iPint
3. confirmation that PSP has done KYC/AML check on merchant
4. merchant’s kyc details to be shared with iPint
5. merchant fee (%) to be charged from merchant
6. merchant’s kyc documents
   
   a. Proof of Business Entity: Company's registration document such as Certificate of Incorporation or business license issued by the government.
   
   b. Proof of Business Address: Current utility bill or business lease/ rental agreement.
   
   c. Photo ID of Beneficial Owner: Passport or National ID.
   
   d. Bank Statement: 3 Months bank statements to be submitted.
   
   e. Tax ID documents: EIN Verification document, IRS letter or W9 form, or previously filed tax return form. [ For US, Netherlands & German Merchants]

For requirements in above 5 points, you need to call [/aggregator/merchants](#aggregator_merchant_onboarding) endpoint

To share merchant’s kyc documents, get aws s3 link from [/merchant/account](#aggregator_merchant_docs) endpoint.

#### Step 3
Provide Merchant's Settlement Info at [/preferences](#aggregator_merchant_settlement_info) endpoint.

#### Step 4
Check [Integration Options](#integrating_with_ipint)

    
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
- Mainnet Base URL https://api.ipint.io:8003
- Testnet Base URL https://api.ipint.io:8002

#### <a name="checkout_endpoint">Checkout</a>
To get id to be fetched in the redirect url.

Mainnet Redirect URL : https:ipint.io/checkout?id=fetch-id-from-the-response

Testnet Redirect URL : https:ipint.io/test-checkout?id=fetch-id-from-the-response
* ###### URL
  /checkout
* ###### Method
  POST
* ###### Headers
  apikey: your-api-key
* ###### URL Params
  None
* ###### Data Params
  *Required:* client_email_id, client_preferred_fiat_currency, merchant_website
  
  *Conditional:* merchant_id, to be used if B2B
  
  *Optional:* amount
  
  ```
  {
      "client_email_id": "client-email-address-for-unique-reference-id",
      "client_preferred_fiat_currency": "local-currency-code",
      "amount": "amount-in-local-currency-upto-two-decimal-places",
      "merchant_id": "ipint-merchant-id",
      "merchant_website": "redirect-url-page-where-you-want-to-redirect-the-customer"
  }
  ```
  curl sample request
  ```
  curl --location --request POST 'https://api.ipint.io:8003/checkout' \
  --header 'apikey: your-api-key' \
  --header 'Content-Type: application/json' \
  --data-raw '{
      "client_email_id": "user@email.id",
      "client_preferred_fiat_currency": "INR",
      "amount": "4999.65",
      "merchant_website": "https://merchant.redirect"
  }'
  ```
* ###### Success Response
  *Code*: 200
  
  ```
  {"session_id":"id-to-be-fetched-in-the-url-to-redirect-to-ipint"}
  ```
  sample response
  ```
  {"session_id":"voZ3pYmiE16FLaSfFmytouwFfcjR4Zom8"}
  ```
  *Note*: Use this id <br />1. To redirect to the iPint checkout page.<br />2. To get status after the payment (call [/invoice](#invoice_endpoint) endpoint)
* ###### Error Response
  *Code*: 400
  ```
  {"error": true, "message": "error-messsage"}
  ```


#### <a name="invoice_endpoint">Invoice</a>
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
curl --location --request GET 'https://api.ipint.io:8003/invoice?id=id-from-the-response-of-checkout-endpoint' \
--header 'content-type: application/json' \
--header 'apikey: your-api-key' \
--header 'signature: hmac-signature-using-your-api-secret' \
--header 'nonce: current-unix-time' 
```
sample response
```
{'data': {'blockchain_confirmations': '0/6',
  'blockchain_transaction_status': 'PENDING',
  'depositor_email_id': 'user@email.id',
  'invoice_amount_in_local_currency': '4999.65',
  'invoice_amount_in_usd': '67.58',
  'invoice_creation_time': '1633960704',
  'invoice_crypto_amount': '0.11065324',
  'invoice_id': 'voZ3pYmiE16FLaSfFmytouwFfcjR4Zom8',
  'local_currency_code': 'INR',
  'received_amount_in_local_currency': '4999.65',
  'received_amount_in_usd': '67.58',
  'received_crypto_amount': '0.11065324',
  'transaction_crypto': 'BCH',
  'transaction_hash': '',
  'transaction_onclick': '',
  'transaction_status': 'CHECKING',
  'transaction_time': '',
  'wallet_address': 'qrk2dnecws4c6fm2av0mkuf5g4d4wm7lrcr0k2lmrh'},
 'user_data': {'company_name': 'merchant-name',
  'depositor_id': 'user@email.id',
  'email_id': 'user.receipt@email.id',
  'website': '#'}}
```
*Note* : 
1. To mark customer's payment, see *transaction_status* in the response from /invoice (GET).
  
   *CHECKING* means checking for transation on blockchain

   *PROCESSING* means transaction hit blockchain but confirmations are less than 3, see blockchain_confirmations in the response

   *COMPLETED* means transaction is completed, you can mark the payment successful

   *FAILED* means customer didn't pay the invoice or transaction is pending on blockchain, see blockchain_transaction_status in the response

2. To check whether invoice is full in paid, compare invoice_amount_in_local_currency, invoice_amount_in_usd, invoice_crypto_amount with received_amount_in_local_currency, received_amount_in_usd, received_crypto_amount respectively

#### <a name="aggregator_api_reference">For Aggregator/PSP Only</a>
##### <a name="aggregator_merchant_onboarding">Onboard Merchant</a>
To onboard a merchant of an aggregator
* ###### URL
  /aggregator/merchants
* ###### Method
  POST
* ###### Headers
  content-type: application/json
  
  apikey: your-api-key
  
  signature: [hmac-signature-using-your-api-secret](#example_code_for_authentcated_endpoints)
  
  nonce: current-unix-time
* ###### URL Params
  None
* ###### Data Params
  All the details are of your merchant business.
  
  Sample Data
  ```
  {
    "legal_name_of_business": "Legal Name of Merchant Business",
    "business_registration_country": "Name of Country of Merchant Business Registration",
    "legal_status_of_business": "e.g. Sole Trader", // options: Sole Trader, Partnership, Incorporated Company, Government Organization, Non-Profit
    "business_type": "e.g. B2B",  // options: B2B, B2C, Both
    "industry": "e.g. Financial Services",  // you can check at https://api.ipint.io:8003/industries
    "annual_revenue": "e.g. $ 0-1 Million",  // options: $ 0-1 Million, $ 1-25 Million, $ 25-100 Million, $ More than 100 Million
    "trade_name_of_service": "Trade Name of Merchant Business",
    "business_registration_number": "Business Registration Number",
    "main_business_activity": "Main Activity of Business",
    "target_website": "Website where API to be used",
    "date_of_incorporation": "Date of Merchant Company Incorporation",  // Date format : YYYY-MM-DD
    "expected_maximum_amount_single_transaction": "e.g.10000", // in usd
    "expected_yearly_transaction_volume": "e.g.10000000",  // in usd
    "merchant_tool": "API",
    "legally_registered_business_address": {
      "house_number": "e.g.23",
      "street_name1": "e.g.near saw mill",
      "street_name2": "e.g.Lotus Vihar",
      "city": "e.g.Philadelphia",
      "state": "e.g.Pennsylvania",
      "postal_code": "e.g.19092"
    },
    "contact": {
      "phone_number": "e.g.79832689342",
      "country_phone_code": "e.g.+1",
      "website": "e.g.abc.co",
      "support_email": "e.g.support@abc.co",
      "notification_email": "e.g.support@abc.co"
    },
    // Merchant Business Beneficial Owner Info
    "business_beneficial_owner": {
      "first_name": "First Name",
      "last_name": "Last Name",
      "date_of_birth": "Date of Birth",  // Date Format : YYYY-MM-DD
      "relation_with_organization": "e.g. Managing Director/CEO",  // options: Managing Director/CEO, Director, Promoter, Principal, Other
      "other_relationship": "mention other relationship",  // if Other
      "email": "e.g.benef@email.com",
      "country_phone_code": "e.g.+91",
      "phone_number": "e.g.7835211996"
    },
    "merchant_fee": "fee (in percentage, int or float value) to be charged from merchant",
    "is_merchant_kyc_done_by_psp": "YES",  // confirmation by aggregator
    "can_psp_share_merchant_data_with_ipint": "YES",  // confirmation by merchant - "We agree that you can share our 'Merchant KYC Details' with iPint."
    "is_merchant_agreed_with_ipint_policies": "YES"  // confirmation by merchant - "We agree with iPint's Terms of Service (https://ipint.io/terms-services.html), Privacy Policy (https://ipint.io/privacy-policy.html) and AML/CFT Policy (https://ipint.io/aml-cft-policy.html)."
  }


* ###### Response 200
     {"message": "OK", "merchant_id": "Merchant ID"}
 
* ###### Response 400
     {"error": true, "message": "description for error"}

##### <a name="aggregator_merchant_docs">Merchant KYC Documents</a>
To share merchant’s kyc documents, get aws s3 link. Get a new link to share each document.
* ###### URL
  /merchant/account
* ###### Method
  GET
* ###### Headers
  content-type: application/json
  
  apikey: your-api-key
  
  signature: [hmac-signature-using-your-api-secret](#example_code_for_authentcated_endpoints)
  
  nonce: current-unix-time
* ###### URL Params
  id : Merchant ID
  
  key : filename  e.g. Proof of Business Entity
* ###### Data Params
  None
  
* ###### Response 200
     {"presigned_url": presigned url to upload a single document}
 
* ###### Response 400
     {"error": true, "message": "description for error"}

##### <a name="aggregator_merchant_settlement_info">Merchant Settlement Info</a>
To provide settlement info for a merchant of an aggregator
* ###### URL
  /preferences
* ###### Method
  POST - to be called only once per merchant to add settlement info
  
  PUT - to update merchant settlement info
* ###### Headers
  content-type: application/json
  
  apikey: your-api-key
  
  signature: [hmac-signature-using-your-api-secret](#example_code_for_authentcated_endpoints)
  
  nonce: current-unix-time
* ###### URL Params
  None
* ###### Data Params
  Merchant wallet details to do settlement.
  
  Sample Data
  ```
  {
    "merchant_id": "Merchant ID",
    "settlement_accounts": [
      {
        "wallet_title": "My USDT Wallet",
        "address": "0x00556a16efc8bbfa9cd462c4ae31bee126efcba2",
        "currency": "USDT",
        "percentage": "100%"
      }
    ],
    "settlement_cycle": "Weekly"  // options: Daily, Weekly, Monthly
  }


* ###### Response 200
     {"message": "OK"}
 
* ###### Response 400
     {"error": true, "message": "description for error"}
     
#### <a name="example_code_for_authentcated_endpoints">Example code for authenticated endpoints</a>
##### <a name="java_code">Java</a>
<a href="https://github.com/devbitfia/iPint/tree/main/java-client">Check this directory for java code</a>

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
