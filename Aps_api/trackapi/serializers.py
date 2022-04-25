from django.contrib.auth.models import User, Group
from rest_framework import serializers
from .models import Employee


class EmployeeSerializer(serializers.HyperlinkedModelSerializer):
    department = serializers.SlugRelatedField(
        many=False,
        read_only=True,
        slug_field='name'
    )
    salary = serializers.StringRelatedField()
    location = serializers.StringRelatedField()

    class Meta:
        model = Employee
        fields = ['id', 'f_name', 'l_name', 'job_name', 'hire_date',
                  'department', 'salary', 'status', 'location']
