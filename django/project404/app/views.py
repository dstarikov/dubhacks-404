from django.shortcuts import render
from django.http import HttpResponse
import pandas as pd
import numpy as np
import requests
from fuzzywuzzy import fuzz
#from iex import Stock
# key = full company name -> tuple of info ( ticker, sector, industry)
companiesMap = {}


def index(request):
    return HttpResponse("This is a test page")

def blackrockTest(request):
    print('GET list:',request.GET.getlist('text'))
    ticker = companyToTicker(request.GET.getlist('text'))
    print('Ticker chosen:',ticker)
    if ticker is None:
        return HttpResponse("Server Returned 420: That's not a company, fam...")
    with open('app/page/pageBegin.html', 'r') as f:
        data1 = f.read()
    data2 = blackrockPerformance(ticker)
    with open('app/page/pageMiddle1.html', 'r') as f:
        data3 = f.read()
    with open('app/page/pageMiddle2.html', 'r') as f:
        data5 = f.read()
    data6 = blackrockSecurityData(ticker)
    with open('app/page/pageEnd.html', 'r') as f:
        data7 = f.read()
    return HttpResponse(data1+data2+data3+data5+data6+data7)

#def testStock():
#    print(Stock("AAPL").price())

def buildCompaniesMap():
    """
    builds a map of company names using the
    pandas data frame
    """
    df = pd.read_csv('../../secwiki_tickers.csv')
    # print (df)
    for index, row in df.iterrows():
        companyName = row["name"]
        # print (companyName)
        ticker = row["ticker"]
        sector = row["sector"]
        industry = row["industry"]
        companiesMap[companyName] = (ticker, sector, industry)

def companyToTicker(companies):
    weight = 0
    current = None
    for company in companies:
        for key, value in companiesMap.items():
            currweight = fuzz.token_set_ratio(key, company.lower())
            if currweight > weight :
                weight = currweight
                current = value[0]
    if weight < 80:
        return None
    return current

def blackrockPerformance(ticker):
    output = requests.get(
            url="https://www.blackrock.com/tools/hackathon/performance",
            params={ 'identifiers' : ticker })
    return output.text

def blackrockSecurityData(ticker):
    output = requests.get(
            url="https://www.blackrock.com/tools/hackathon/security-data",
            params={ 'identifiers' : ticker })
    return output.text

buildCompaniesMap()
#testStock()
