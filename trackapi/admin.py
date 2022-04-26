from django.contrib import admin

# Register your models here.
from .models import Department
from .models import Salary
from .models import Location
from .models import Employee

admin.site.register(Department)
admin.site.register(Salary)
admin.site.register(Location)
admin.site.register(Employee)
