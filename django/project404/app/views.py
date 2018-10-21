# from django.shortcuts import render
# from django.http import HttpResponse
import pandas as pd
import numpy as np
import requests
from fuzzywuzzy import fuzz

# key = full company name -> tuple of info ( ticker, sector, industry)
companiesMap = {}


def index(request):
    return HttpResponse("This is a test page")

def blackrockTest(request):
    output = requests.get(
            url="https://www.blackrock.com/tools/hackathon/performance",
            params={
                'identifiers' : 'AAPL',
                #exclude unused fields
                'startDate' : '20180101',
                'returnsType' : 'MONTHLY',
            })
    return HttpResponse(output.success)


def buildCompaniesMap():
    """
    builds a map of company names using the
    pandas data frame
    """
    df = pd.read_csv('../../../secwiki_tickers.csv')
    # print (df)
    for index, row in df.iterrows():
        companyName = row["name"]
        # print (companyName)
        ticker = row["ticker"]
        sector = row["sector"]
        industry = row["industry"]
        companiesMap[companyName] = (ticker, sector, industry)

def companyToTicker(company):
    for key, value in companiesMap.items():

        if fuzz.token_set_ratio(key, company.lower()) > 80 :
            print(value[0])
            return value[0]

    return None

def blackrockPerformance():
    pass

def blackrockPortfolio():
    pass

def blackrockSearchSecurities():
    pass

def blackrockSecurityData():
    pass

buildCompaniesMap()

# Create your views here.
