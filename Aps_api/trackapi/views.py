from django.shortcuts import render
from .models import Employee
from rest_framework import viewsets
from .serializers import EmployeeSerializer
# Create your views here.


class EmployeeViewSet(viewsets.ModelViewSet):
    """
    API endpoint that allows users to be viewed or edited.
    """
    queryset = Employee.objects.all()
    serializer_class = EmployeeSerializer
