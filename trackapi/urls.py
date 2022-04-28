from django.contrib import admin
from django.urls import path
from trackapi import views
from rest_framework.urlpatterns import format_suffix_patterns
from rest_framework.authtoken import views as auth_views

urlpatterns = [
    path('employees/', views.EmployeeList.as_view()),
    path('employee/<str:pk>/', views.EmployeeDetail.as_view()),
    path('users/', views.UserList.as_view()),
    path('users/<int:pk>/', views.UserDetail.as_view()),
]

urlpatterns = format_suffix_patterns(urlpatterns)
