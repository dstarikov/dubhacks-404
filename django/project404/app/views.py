from django.shortcuts import render
from django.http import HttpResponse

def index(request):
    return HttpResponse("This is a test page")

# Create your views here.
