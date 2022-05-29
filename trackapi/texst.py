import datetime
tnow = datetime.datetime.now()
y = tnow.date().year
m = tnow.date().month
d = tnow.date().day
if datetime.datetime(y, m, d, 18, 00, 00, 0000) <= tnow:
    print("me")
print("you")
