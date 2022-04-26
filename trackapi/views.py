from rest_framework import permissions
from .models import Employee
from rest_framework.decorators import api_view, APIView
from .serializers import EmployeeSerializer, UserSerializer
from rest_framework import generics
from django.contrib.auth.models import User
# Create your views here.


class EmployeeList(generics.ListCreateAPIView):
    """
    List all employee, or create a new employee record.
    """
    queryset = Employee.objects.all()
    serializer_class = EmployeeSerializer
    permission_classes = [permissions.IsAuthenticatedOrReadOnly]

    def perform_create(self, serializer):
        serializer.save(owner=self.request.user)


class EmployeeDetail(generics.RetrieveUpdateDestroyAPIView):
    """
    Retrieve, update or delete an employee record.
    """
    queryset = Employee.objects.all()
    serializer_class = EmployeeSerializer
    permission_classes = [permissions.IsAuthenticatedOrReadOnly]


class UserList(generics.ListAPIView):
    queryset = User.objects.all()
    serializer_class = UserSerializer


class UserDetail(generics.RetrieveAPIView):
    queryset = User.objects.all()
    serializer_class = UserSerializer
