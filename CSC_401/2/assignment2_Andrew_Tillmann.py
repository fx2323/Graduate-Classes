#Andrew Tillmann's Assignment #2

#3.21 part (f)
def forLoops():
    'Use function range() and prints sequence.'
    for i in range(5,22,4):
        print(i)

#3.32
def pay(hourlyWage, numHours):
    "Computes and returns the employee's pay."
    overtime=numHours-40
    if overtime>0:
        pay=(overtime*1.5*hourlyWage)+40*hourlyWage
    else:
        pay=hourlyWage*numHours
    return pay
    
#3.36
def abbreviation(weekday):
    "Takes day of week as input and returns its two-letter abbrevation."
    return (weekday[0]+weekday[1])
    
    
#3.37
def collision(x1, y1, r1, x2, y2, r2):
    "Check whether two circular objects colide. Returns True if they do."
    #need to import math for sqrt() function
    import math
    distance=math.sqrt((x1-x2)**2+(y1-y2)**2)
    rDistance=r1+r2

    return distance<=rDistance
    
#3.38
def partition(lst):
    "Splits a list of soccer players into two groups."
    for i in range(len(lst)):
        if 'A'<=lst[i][0] and lst[i][0]<='M':
            print(lst[i])
