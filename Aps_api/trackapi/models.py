from django.db import models
# Create your models here.


class Department(models.Model):
    name = models.CharField(blank=False, null=False, max_length=70)


class Salary(models.Model):
    basic_salary = models.IntegerField()
    commission = models.IntegerField()

    def __str__(self) -> str:
        return 'Basic Salary: %d \nCommission: %d \nTotal: %d' % (self.basic_salary, self.commission, (self.basic_salary+self.commission))


class Location(models.Model):
    one_hour = models.CharField(blank=True, null=True, max_length=100)
    two_hours = models.CharField(blank=True, null=True, max_length=100)
    three_hours = models.CharField(blank=True, null=True, max_length=100)

    def __str__(self) -> str:
        return 'One hour: %s, Two hours: %s, Three hours: %s' % (self.one_hour, self.two_hours, self.three_hours)


class Employee(models.Model):
    id = models.CharField(blank=False, null=False,
                          max_length=100, primary_key=True)
    f_name = models.CharField(blank=False, null=False, max_length=70)
    l_name = models.CharField(blank=False, null=False, max_length=70)
    job_name = models.CharField(blank=False, null=False, max_length=50)
    hire_date = models.DateField(blank=False)
    status = models.CharField(max_length=4)
    salary = models.ForeignKey(Salary, on_delete=models.CASCADE)
    department = models.ForeignKey(Department, on_delete=models.CASCADE)
    location = models.ForeignKey(Location, on_delete=models.CASCADE)
