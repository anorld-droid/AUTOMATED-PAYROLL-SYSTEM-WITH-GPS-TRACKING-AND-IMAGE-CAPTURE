from dataclasses import fields
import datetime
from django.contrib.auth.models import User, Group
from rest_framework import serializers
from .models import Department, Employee, Onsite, Salary, Offsite


class UserSerializer(serializers.ModelSerializer):
    employees = serializers.PrimaryKeyRelatedField(
        many=True, queryset=Employee.objects.all())

    class Meta:
        model = User
        fields = ['id', 'username', 'employees']


class SalarySerializer(serializers.ModelSerializer):
    class Meta:
        model = Salary
        fields = ['basic_salary', 'commission']


class OnsiteSerializer(serializers.ModelSerializer):
    class Meta:
        model = Onsite
        fields = ['onsite_time']

    def to_representation(self, value):
        return value.onsite_time


class OffsiteSerializer(serializers.ModelSerializer):
    class Meta:
        model = Offsite
        fields = ['offsite_time']

    def to_representation(self, value):
        return value.offsite_time


class DepartmentSerielizer(serializers.ModelSerializer):
    class Meta:
        model = Department
        fields = ['name']


class EmployeeSerializer(serializers.HyperlinkedModelSerializer):
    owner = serializers.ReadOnlyField(source='owner.username')
    department = DepartmentSerielizer()
    salary = SalarySerializer()
    onsites = OnsiteSerializer(many=True)
    offsites = OffsiteSerializer(many=True)

    class Meta:
        model = Employee
        fields = ['owner', 'id', 'first_name', 'last_name', 'image', 'job_name', 'hire_date',
                  'status', 'department', 'salary', 'onsites', 'offsites']

    def create(self, validated_data):
        department = Department.objects.create(
            **validated_data.pop('department'))
        salary = Salary.objects.create(**validated_data.pop('salary'))
        onsites = Onsite.objects.create(**validated_data.pop('onsites'))
        offsites = Offsite.objects.create(**validated_data.pop('offsites'))
        employee = Employee.objects.create(
            **validated_data, department=department, salary=salary, onsites=onsites, offsites=offsites)
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

        if 'department' in validated_data:
            department_data = validated_data.pop('department')
            department = instance.department
            department.name = department_data.get('name', department.name)
            department.save()

        if 'salary' in validated_data:
            salary_data = validated_data.pop('salary')
            salary = instance.salary
            salary.basic_salary = salary_data.get(
                'basic_salary', salary.basic_salary)
            salary.commission = salary_data.get(
                'commission', salary.commission)
            if not instance.status:
                tnow = datetime.datetime.now()
                y = tnow.date().year
                m = tnow.date().month
                d = tnow.date().day
                if datetime.datetime(y, m, d, 16, 00, 00, 0000) <= tnow:
                    timed = tnow - datetime.datetime(y, m, d, 11, 00, 00, 0000)
                    tsec = timed.total_seconds()
                    thour = tsec / 3600
                    comm = thour * 3600
                    salary.commission += comm
            salary.save()
        else:
            salary_data = Employee.objects.get(
                id=instance.id).salary
            salary = instance.salary
            salary.basic_salary = salary_data.basic_salary
            salary.commission = salary_data.commission
            if not instance.status:
                tnow = datetime.datetime.now()
                y = tnow.date().year
                m = tnow.date().month
                d = tnow.date().day
                if datetime.datetime(y, m, d, 16, 00, 00, 0000) <= tnow:
                    timed = tnow - datetime.datetime(y, m, d, 11, 00, 00, 0000)
                    tsec = timed.total_seconds()
                    thour = tsec / 3600
                    comm = thour * 3600
                    salary.commission += comm
            salary.save()

        tnow = f"{datetime.datetime.now()}"
        if 'onsites' in validated_data:
            onsite_data = validated_data.pop('onsites')
            onsites = []
            for time in onsite_data:
                onsite_instance, onsite_created = Onsite.objects.update_or_create(
                    pk=time.get('id'), defaults=time)
                if onsite_created:
                    onsites.append(onsite_instance.pk)
            if instance.status:
                onsite_instance = Onsite.objects.create(
                    onsite_time=tnow)
                onsites.append(onsite_instance.pk)
                instance.onsites.set(onsites)
        else:
            sites = []
            onsite_lis = Employee.onsites.through.objects.filter(
                employee_id=instance.id)
            for site in onsite_lis:
                sites.append(site.onsite.pk)
            if instance.status:
                onsite_inst = Onsite.objects.create(
                    onsite_time=tnow)
                sites.append(onsite_inst.pk)
                instance.onsites.set(sites)

        if 'offsites' in validated_data:
            offsite_data = validated_data.pop('offsites')
            offsites = []
            for time in offsite_data:
                offsite_instance, offsite_created = Offsite.objects.update_or_create(
                    pk=time.get('id'), defaults=time)
                if offsite_created:
                    offsites.append(offsite_instance.pk)
            if not instance.status:
                offsite_instance = Offsite.objects.create(offsite_time=tnow)
                offsites.append(offsite_instance.pk)
            instance.offsites.set(offsites)
        else:
            off_sites = []
            offsite_lis = Employee.offsites.through.objects.filter(
                employee_id=instance.id)
            for off_site in offsite_lis:
                off_sites.append(off_site.offsite.pk)
            if not instance.status:
                offsite_inst = Offsite.objects.create(
                    offsite_time=tnow)
                off_sites.append(offsite_inst.pk)
                instance.offsites.set(off_sites)
        instance.save()

        return instance
