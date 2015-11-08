#5.41
def poly(coef, x):
    '''returns the evaluation of the polynomial with
       coefficients in coef on value x'''

    total=0
    
    for i in range(len(coef)):
        total += coef[i]*(x**i)
    return total


# 5.47 iterative version
def d2x(n, x):
    'returns base-x representation of n'

    string='0'

    #iterates n times
    for i in range(n):
        
        #adds one to the begining digit
        string= string[:-1]+str(eval(string[-1])+1)

        for j in range(1,len(string)+1):
            #base was meet
            if eval(string[-j])==x:

                #reset digit to 0
                if j==1:
                    string=string[:-j]+'0'
                else:
                    string= string[:-j]+'0'+string[-j+1:]

                #increase next digit one higher
                try:
                    string[-j-1]
                    
                    #evaluate the string then add one then add it back to the string
                    string=string[:-j-1]+str(eval(string[-j-1])+1)+string[-j:]

                #no next digit in string need to set it to 1
                except:
                    string= "1"+string
    return string
                

# 5.47 recursive version
def d2x2(n, x):
    'returns base-x representation of n'

    #base case
    if n==0:
        string='0'
        return string

    #recurvies function
    else:
        string=d2x2(n-1,x)

    #adds one to the begining digit
    string= string[:-1]+str(eval(string[-1])+1)

    #iratates over string and checks if base-x was meet
    for j in range(1,len(string)+1):

        #base-x was meet
        if eval(string[-j])==x:

            #reset digit to 0 that is at base-x
            if j==1:
                string=string[:-j]+'0'
            else:
                string= string[:-j]+'0'+string[-j+1:]

            #increase next digit one higher
            try:
                string[-j-1]
                
                #evaluate the string then add one then add it back to the string
                string=string[:-j-1]+str(eval(string[-j-1])+1)+string[-j:]

            #no next digit in string need to set it to 1
            except:
                string= "1"+string

    return string
    


def combinations(n, k):
    'returns the number of ways to choose k out of n items'

    if n<k:
        return 0
    elif k==0:
        return 1
    else:
        return combinations(n-1,k-1)+combinations(n-1,k)


def pascal(n):
    '''returns a list containing the sequence of numbers
       in the n-th line of Pascal's triangle'''
    #base case
    if n==0:
        lst=[1]
        return lst
    else:
        lst=pascal(n-1)
        newLst=[1]
        #adds items to newLst from lst n-1 add two numbers at a time
        for i in range(len(lst)-1):
            newLst.append(lst[i]+lst[i+1])
        newLst.append(1)
    return newLst


def traverse(path, d):
    '''if path refers to a folder, prints the pathname
       of every file or folder contained directly or indirectly
       in folder path'''

    import os
    try:
        lst=os.listdir(path)
        #need to make it does it does not print input folder
        if d!=0:
            #need to print folder and offset by one
            print((d-1)*'  '+path)

        #for each item indirectory run traverse
        for i in range(len(lst)):
            traverse(path+'/'+lst[i],d+1)
    except:
        #need to print file and offset by one
        print((d-1)*'  ' +path)
