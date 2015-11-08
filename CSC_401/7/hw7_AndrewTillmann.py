#5.36
def prime(n):
    'returns True if n is prime and False if not'

    for i in range(2,n):
        if n%i==0:
            #if factors is found return false
            return False
    #no factors found
    return True

#5.43
def evenrow(table):
    'takes 2-dimensinoal list of integers and returns True if each row of the table sums up to an even number'

    for i in range(len(table)):
        count=0
        #adds up all the intergers in sublists
        for j in range(len(table[i])):
            count += table[i][j]
        #if sublist total is not even stop program and return False
        if count%2!=0:
            return False
    #Since no False was return must return True
    return True

#10.18
def numOnes(n):
    'takes nonnegative integer and returns the numbers 1s in binary representation of n'

    #base case
    if n==0:
        return 0
    #n is odd
    elif (n%2)!=0:
        return numOnes(n-1)+1
    else:
        return numOnes(n//2)
#10.19
def rgcd(a, b):
    'takes two nonnegative numbers a and b, a>b and returns the GCD of a and b'
    if b==0:
        return a
    else:
        return rgcd(b,a%b)

#10.27
def crawl(filename):
    'crawls though linked files'
    #prints message of visting file
    print('Visiting  {}'.format(filename))
    #opens file and gets all the lines in a list then closes file
    infile=open(filename,'r')
    content=infile.readlines()
    infile.close()
    

    for i in range(len(content)):
        content[i]=content[i].replace('\n','')
        crawl(content[i])
#10.31a      
def levy(n):
    'returns the tutle instructions(L,R,F) for drawing the Levy curve'

    if n > 0:
        string=levy(n-1)
        if "F" in string:
            string=string.replace('F','LFRFL')
        return string
    #base case
    elif n==0:
        return 'F'
#10.31b
def drawLevy(n):
    'takes string from levy and returns drawing'

    import turtle
    s=turtle.Screen()
    t=turtle.Turtle()
    t.pendown()
    directions=levy(n)
    for i in range(len(directions)):
        if directions[i]=='F':
            t.fd(5)
        if directions[i]=='L':
            t.lt(45)
        if directions[i]=='R':
            t.rt(90)
    s.bye()
