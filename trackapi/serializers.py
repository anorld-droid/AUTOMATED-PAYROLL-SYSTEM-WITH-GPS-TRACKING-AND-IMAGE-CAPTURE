from dataclasses import fields
from django.contrib.auth.models import User, Group
from rest_framework import serializers
from .models import Department, Employee, Salary, Location


class SalarySerializer(serializers.ModelSerializer):
    class Meta:
        model = Salary
        fields = ['basic_salary', 'commission']


class LocationSerializer(serializers.ModelSerializer):
    class Meta:
        model = Location
        fields = ['one_hour', 'two_hours', 'three_hours']


class DepartmentSerielizer(serializers.ModelSerializer):
    class Meta:
        model = Department
        fields = ['name']


class EmployeeSerializer(serializers.HyperlinkedModelSerializer):
    department = DepartmentSerielizer()
    salary = SalarySerializer()
    location = LocationSerializer()

    class Meta:
        model = Employee
        fields = ['id', 'first_name', 'last_name', 'image', 'job_name', 'hire_date',
                  'status', 'department', 'salary', 'location']

    def create(self, validated_data):
        department = Department.objects.create(
            **validated_data.pop('department'))
        salary = Salary.objects.create(**validated_data.pop('salary'))
        location = Location.objects.create(**validated_data.pop('location'))
        employee = Employee.objects.create(
            **validated_data, department=department, salary=salary, location=location)
        return employee

    def update(self, instance, validated_data):
        instance.id = validated_data.get('id', instance.id)
        instance.first_name = validated_data.get(
            'first_name', instance.first_name)
        instance.last_name = validated_data.get(
            'last_name', instance.last_name)
        instance.image = validated_data.get('image', instance.image)
        instance.job_name = validated_data.get('job_name', instance.job_name)
        instance.status = validated_data.get('status', instance.status)
        instance.save()
        department_data = validated_data.pop('department')
        department = instance.department
        department.name = department_data.get('name', department.name)
        department.save()
        salary_data = validated_data.pop('salary')
        salary = instance.salary
        salary.basic_salary = salary_data.get(
            'basic_salary', salary.basic_salary)
        salary.commission = salary_data.get('commission', salary.commission)
        salary.save

        return instance
