# Messages Exchanged with LIS 

## LIS Sending Sample to Labotron

```
{
    "sampleId": "<string>",
    "sampleType": "<string>",
    "patientId": "<string>",
    "firstName": "<string>",
    "lastName": "<string>",
    "middleInitial": "<string>",
    "dateOfBirth": "<ISO8601_date_string>",
    "sex": "<string>",
    "currentDateTime": "<ISO8601_date_string>",
    "collectionDate": "<ISO8601_date_string>",
    "origin": "<string>",
    "tests": [
        {
            "testId": "<string> : actualTestId",
            "testCode": "<string>",
            "testDesc": "<string>",
            "status": "<string>",
            "analyzerCode": "<string>",
            "actualAnalyzerCode": "<string>",
            "alarm": "<boolean>",
            "result": "<number|string>",
            "notes": "<string>",
            "unit": "<string>",
            "message": "<string>",
            "priority": "<string>",
            "verificationPending": "<boolean>"
        }
    ]
}
```

## Labotron returning messages to LIS

```
{
    "sampleId": "<string>",        
    "testId": "<string> : actualTestId",
    "status": "<string>",
    "actualAnalyzerCode": "<string>",
    "alarm": "<boolean>",
    "result": "<number|string>",
    "notes": "<string>",
    "unit": "<string>",
    "message": "<string>",
    "verificationPending": "<boolean>"
}
```
