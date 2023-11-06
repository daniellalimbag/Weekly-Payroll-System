import java.util.*
val scanner = Scanner(System.`in`)

fun main() {
    val payrollConfig = PayrollConfig(
        dailyRate = 500.0f,
        maxHours = 8,
        workdays = 5,
        defDayType = "Normal",
        defInTime = "0900",
        defOutTime = "1700"
    )
    val weeklyWorkRecord = WeeklyWorkRecord()
    val payrollCalculator = PayrollCalculator(payrollConfig, weeklyWorkRecord)
    mainMenu(payrollCalculator)
}

fun mainMenu(payrollCalculator: PayrollCalculator) {
    while (true) {
        println("----------------------------------------------------")
        println("Weekly Payroll System")
        println("[1] Calculate Total Salary")
        println("[2] Edit Configurations")
        println("[3] Calculate Daily Salary (Test)")
        println("[4] Exit")
        print("Enter your choice: ")

        when (scanner.nextInt()) {
            1 -> calculateTotalSalary(payrollCalculator)
            2 -> editConfigurations(payrollCalculator)
            3 -> calculateDailySalary(payrollCalculator)
            4 -> {
                println("Exiting the Payroll System.")
                return
            }
            else -> println("Invalid choice. Please select a valid option.")
        }
        println("----------------------------------------------------")
    }
}

fun calculateTotalSalary(payrollCalculator: PayrollCalculator) {
    for (i in 1..7) {
        println("----------------------------------------------------")
        print("[$i] ")
        addDailyWorkRecord(payrollCalculator)
        println("----------------------------------------------------")
    }
    payrollCalculator.calculateTotalSalary()
    var i = 0
    for (day in payrollCalculator.weeklyWorkRecord.records) {
        println("Day #$i Salary = ${day.salary}")
    }
    println("Total Salary for the week: ${payrollCalculator.weeklyWorkRecord.totalSalary}")
}

fun addDailyWorkRecord(payrollCalculator: PayrollCalculator) {
    print("[Add Daily Work Record]")
    print("\nEnter OUT time (HHmm): ")
    val outTime = scanner.next()
    println("Enter dayType: ")
    val days = listOf("Normal", "Rest Day", "Regular Holiday", "Special Non-Working Day", "Special Non-Working Day and Rest Day")
    for ((index, day) in days.withIndex()) {
        println("[$index] $day")
    }
    val input = scanner.nextInt()
    val selectedDayType = if (input in 0 until days.size) {
        days[input]
    } else {
        println("Invalid input. Using 'Normal' as the default day type.")
        "Normal"
    }
    val dailyWorkRecord = DailyWorkRecord(
        payrollConfig = payrollCalculator.payrollConfig,
        inTime = payrollCalculator.payrollConfig.defInTime,
        outTime = outTime,
        dayType = selectedDayType,
        regOvertimeHrs = 0,
        nsHrs = 0,
        salary = 0.0f,
        isAbsent = false
    )
    payrollCalculator.weeklyWorkRecord.records.add(dailyWorkRecord)
    println("Daily Work Record added successfully.")
}

fun editConfigurations(payrollCalculator: PayrollCalculator) {
    println("----------------------------------------------------")
    println("[Edit Configurations]")
    println("Current Configuration:")
    println("[1] Daily Rate: ${payrollCalculator.payrollConfig.dailyRate}")
    println("[2] Maximum Hours per Day: ${payrollCalculator.payrollConfig.maxHours}")
    println("[3] Workdays per Week: ${payrollCalculator.payrollConfig.workdays}")
    println("[4] Default Day Type: ${payrollCalculator.payrollConfig.defDayType}")
    println("[5] Default In Time (HHmm): ${payrollCalculator.payrollConfig.defInTime}")
    println("[6] Default Out Time (HHmm): ${payrollCalculator.payrollConfig.defOutTime}")
    println("[7] Exit to Main Menu")

    print("Enter the option number to edit (1-6): ")
    when (scanner.nextInt()) {
        1 -> {
            print("Enter new Daily Rate: ")
            val newDailyRate = scanner.nextFloat()
            payrollCalculator.payrollConfig.dailyRate = newDailyRate
            println("Daily Rate updated successfully.")
        }
        2 -> {
            print("Enter new Maximum Hours per Day: ")
            val newMaxHours = scanner.nextInt()
            payrollCalculator.payrollConfig.maxHours = newMaxHours
            println("Maximum Hours per Day updated successfully.")
        }
        3 -> {
            print("Enter new Workdays per Week: ")
            val newWorkdays = scanner.nextInt()
            payrollCalculator.payrollConfig.workdays = newWorkdays
            println("Workdays per Week updated successfully.")
        }
        4 -> {
            print("Enter new Default Day Type: ")
            val newDayType = scanner.next()
            payrollCalculator.payrollConfig.defDayType = newDayType
            println("Default Day Type updated successfully.")
        }
        5 -> {
            print("Enter new Default In Time (HHmm): ")
            val newInTime = scanner.next()
            payrollCalculator.payrollConfig.defInTime = newInTime
            println("Default In Time updated successfully.")
        }
        6 -> {
            print("Enter new Default Out Time (HHmm): ")
            val newOutTime = scanner.next()
            payrollCalculator.payrollConfig.defOutTime = newOutTime
            println("Default Out Time updated successfully.")
        }
        7 -> {
            println("Returning to the Main Menu.")
            return
        }
        else -> {
            println("Invalid option. Please enter a valid option.")
        }
    }
}

fun calculateDailySalary(payrollCalculator: PayrollCalculator) {
    println("[Calculate Daily Salary]")
    print("Enter IN time (HHmm): ")
    val inTime = scanner.next()
    print("Enter OUT time (HHmm): ")
    val outTime = scanner.next()
    println("Enter dayType: ")

    val days = listOf("Normal", "Rest Day", "Regular Holiday", "Special Non-Working Day", "Special Non-Working Day and Rest Day")

    for ((index, day) in days.withIndex()) {
        println("[$index] $day")
    }

    val input = scanner.nextInt()

    val selectedDayType = if (input in 0 until days.size) {
        days[input]
    } else {
        println("Invalid input. Using 'Normal' as the default day type.")
        "Normal"
    }

    val dailyWorkRecord = DailyWorkRecord(
        payrollConfig = payrollCalculator.payrollConfig,
        inTime = payrollCalculator.payrollConfig.defInTime,
        outTime = outTime,
        dayType = selectedDayType,
        regOvertimeHrs = 0,
        nsHrs = 0,
        salary = 0.0f,
        isAbsent = false
    )

    dailyWorkRecord.salary = payrollCalculator.calculateDailySalary(dailyWorkRecord)
    println("Daily Salary: ${dailyWorkRecord.salary}")
}
