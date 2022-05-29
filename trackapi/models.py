from datetime import datetime
from email.policy import default
from django.db import models
# Create your models here.

from django.conf import settings
from django.db.models.signals import post_save
from django.dispatch import receiver
from rest_framework.authtoken.models import Token
from django.contrib.auth.models import User
from rest_framework.authtoken.models import Token


@receiver(post_save, sender=settings.AUTH_USER_MODEL)
def create_auth_token(sender, instance=None, created=False, **kwargs):
    if created:
        Token.objects.create(user=instance)


class Department(models.Model):
    name = models.CharField(blank=False, null=False, max_length=70)


class Salary(models.Model):
    basic_salary = models.IntegerField()
    commission = models.IntegerField()


class Onsite(models.Model):
    onsite_time = models.DateTimeField(blank=False,)


class Offsite(models.Model):
    offsite_time = models.DateTimeField(blank=False,)


def employee_directory_path(instance, filename):
    # file will be uploaded to MEDIA_ROOT / employee_<id>/<filename>
    return 'employee_{0}/{1}'.format(instance.id, filename)


class Employee(models.Model):
    STATUS_CHOICES = [(0, 'OFF'), (1, 'ON')]
    id = models.CharField(blank=False, null=False,
                          max_length=100, primary_key=True, unique=True)
    first_name = models.CharField(blank=False, null=False, max_length=70)
    last_name = models.CharField(blank=False, null=False, max_length=70)
    image = models.ImageField(
        upload_to=employee_directory_path, height_field=None, width_field=None, max_length=100, null=False)
    job_name = models.CharField(blank=False, null=False, max_length=50)
    hire_date = models.DateField(blank=False)
    owner = models.ForeignKey(
        'auth.User', related_name='employees', on_delete=models.CASCADE, null=False)
    status = models.IntegerField(choices=STATUS_CHOICES, default='OFF')
    salary = models.ForeignKey(Salary, on_delete=models.CASCADE)
    department = models.ForeignKey(Department, on_delete=models.CASCADE)
    onsites = models.ManyToManyField(Onsite)
    offsites = models.ManyToManyField(Offsite)

    class Meta:
        ordering = ['hire_date']
