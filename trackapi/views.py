from rest_framework import permissions
from .models import Employee
from .serializers import EmployeeSerializer, UserSerializer
from rest_framework import generics
from django.contrib.auth.models import User
from rest_framework.authtoken.views import ObtainAuthToken
from rest_framework.authtoken.models import Token
from rest_framework.response import Response
from rest_framework.authentication import SessionAuthentication, BasicAuthentication, TokenAuthentication
# Create your views here.


class EmployeeList(generics.ListCreateAPIView):
    """
    List all employee, or create a new employee record.
    """
    authentication_classes = (SessionAuthentication,
                              BasicAuthentication, TokenAuthentication)
    queryset = Employee.objects.all().order_by('id')
    serializer_class = EmployeeSerializer
    permission_classes = [permissions.IsAuthenticatedOrReadOnly]

    def perform_create(self, serializer):
        serializer.save(owner=self.request.user)


class EmployeeDetail(generics.RetrieveUpdateDestroyAPIView):
    """
    Retrieve, update or delete an employee record.
    """
    permission_classes = [permissions.IsAuthenticatedOrReadOnly]
    authentication_classes = (SessionAuthentication,
                              BasicAuthentication, TokenAuthentication)
    queryset = Employee.objects.all().order_by('id')
    serializer_class = EmployeeSerializer


class UserList(generics.ListAPIView):
    queryset = User.objects.all().order_by('id')
    serializer_class = UserSerializer


class UserDetail(generics.RetrieveAPIView):
    queryset = User.objects.all().order_by('id')
    serializer_class = UserSerializer


class CustomAuthToken(ObtainAuthToken):
    def post(self, request, *args, **kwargs):
        serializer = self.serializer_class(data=request.data,
                                           context={'request': request})
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data['user']
        token, created = Token.objects.get_or_create(user=user)
        return Response({
            'token': token.key,
            'user_id': user.pk,
            'email': user.email
        })
