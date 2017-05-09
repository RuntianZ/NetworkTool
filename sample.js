function isPrime (num)

　　{

　　if (num <= 1) {

　　print("Enter an integer no less than 2.")

　　return false

　　}

　　var prime = true

　　var sqrRoot = Math.round(Math.sqrt(num))

　　for (var n = 2; prime & n <= sqrRoot; ++n) {

　　prime = (num % n != 0)

　　}

　　return prime

　　}