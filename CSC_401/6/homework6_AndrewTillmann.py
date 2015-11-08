#Extra
def sieve(n):
    'runs algorithm Sieve of Erastophenes'
    
    L=[]
    primeL=[]

    #adds to list L
    for i in range(2,n):
        L.append(i)

    while  len(L)!=0:
        for i in range(1,len(L)+1):
            if i<len(L): #as objects removed from list need to make sure do not request higher index than exists
                if (L[i]%L[0])==0:
                    L.remove(L[i])

        #adds current prime to prime list and removes it from L
        primeL.append(L[0])
        L.remove(L[0])
    return primeL

#5.49
def heron(n,error):
    'uses method heron to find root of n within error'

    prev=1.0 #given initial guess
    new=((1/2)*(prev+(n/prev)))

    #keeps finding better answer till answer is less than given error away
    while error <= (abs(prev-new)):
        prev = ((1/2)*(new+(n/new)))
        new =((1/2)*(prev+(n/prev)))
    return new

#6.34
def game(n):
    'will ask n number of addition questions'

    import random

    cycle=0
    correct=0
    while cycle< n:
        a=random.randrange(0,9)
        b=random.randrange(0,9)

        print('{} + {} ='.format(a,b))
        question=eval(input('Enter answer: '))
        if question==(a+b):
            print('Correct')
            correct +=1
        else:
            print('Incorrect')
        cycle +=1
    print('You got {} correct answers out of {}'.format(correct,n))

#6.35
def caesar(key,fileName):
    'encodes the file with Caesar cipher'

    #make translation table with cipher
    upperLetters='ABCDEFGHIJKLMNOPQRSTUVWXYZ'
    lowerLetters='abcdefghijklmnopqrstuvwxyz'
    upperKey=''
    lowerKey=''
    
    for i in range(26):
        upperKey += upperLetters[i-26+key] #subtract first since it needs to work from A next Z

        lowerKey += lowerLetters[i-26+key]

    table=str.maketrans(upperLetters+lowerLetters,upperKey+lowerKey)

    #open file and takes content
    infile=open(fileName,'r')
    content=infile.read()
    infile.close()

    #changes the string
    content=content.translate(table)

    #write content to cipher.txt then close it
    infile=open('cipher.txt','w')
    infile.write(content)
    infile.close()

    #lastly print content
    return(content)

#10.12
def countdown3(n):
    'does a countdown with a surprise'

    if n>0:
        print(n)
        #at n==3 run surprise
        if n==3:
            print('BOOM!!!\nScared you...')
        countdown3(n-1)

    #count down finished time for bast off
    else:
        print('Blastoff!!!')

#10.17
def silly(n):
    'prints n questions marks followed by n exlamation points'
    
    if n>0:
        print('?', end='')
        silly(n-1)
        print('!',end='')
