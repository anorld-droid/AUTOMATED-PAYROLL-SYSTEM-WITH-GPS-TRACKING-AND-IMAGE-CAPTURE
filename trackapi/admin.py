from django.contrib import admin

# Register your models here.
from .models import Department
from .models import Salary
from .models import Employee
from .models import Onsite
from .models import Offsite

admin.site.register(Department)
admin.site.register(Salary)
admin.site.register(Onsite)
admin.site.register(Offsite)
admin.site.register(Employee)
