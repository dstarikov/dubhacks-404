from django.shortcuts import render
from django.http import HttpResponse
import requests

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

def companyToTicker(company):
    #TODO
    return None

def blackrockPerformance():
    pass

def blackrockPortfolio():
    pass

def blackrockSearchSecurities():
    pass

def blackrockSecurityData():
    pass

# Create your views here.
