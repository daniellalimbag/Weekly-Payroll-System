import java.util.*
val scanner = Scanner(System.`in`)
val days = listOf("Normal", "Rest Day", "Regular Holiday", "Regular Holiday and Rest Day", "Special Non-Working Day", "Special Non-Working Day and Rest Day")

fun main() {
    val payrollConfig = PayrollConfig(
        dailyRate = 500.0f,
        maxHours = 8,
        workdays = 5,
        defDayType = "Normal",
        defInTime = "0900",
        defOutTime = "0900"
    )
    val weeklyWorkRecord = WeeklyWorkRecord()
    val payrollCalculator = PayrollCalculator(payrollConfig, weeklyWorkRecord)
    mainMenu(payrollCalculator)
}

fun initializeDailyRecords(payrollConfig: PayrollConfig, weeklyWorkRecord: WeeklyWorkRecord) {
    for (i in 0 until 7) {
        val dayType = if (i >= payrollConfig.workdays) "Rest Day" else payrollConfig.defDayType
        weeklyWorkRecord.records.add(
            DailyWorkRecord(
                payrollConfig = payrollConfig,
                inTime = payrollConfig.defInTime,
                outTime = payrollConfig.defOutTime,
                dayType = dayType,
                regOvertimeHrs = 0F,
                nsHrs = 0F,
                salary = 0.0f,
                isAbsent = false,
                isRestDay = i >= payrollConfig.workdays
            )
        )
    }
}

fun mainMenu(payrollCalculator: PayrollCalculator) {
    while (true) {
        println("----------------------------------------------------")
        println("Weekly Payroll System")
        println("[1] Calculate Total Salary")
        println("[2] Edit Configurations")
        println("[3] Display Rates")
        println("[4] Calculate Daily Salary (Test)")
        println("[5] Exit")
        print("Enter your choice: ")

        when (scanner.nextInt()) {
            1 -> calculateTotalSalary(payrollCalculator)
            2 -> editConfigurations(payrollCalculator)
            3 -> displayRates()
            4 -> calculateDailySalary(payrollCalculator)
            5 -> {
                println("Exiting the Payroll System.")
                return
            }
            else ->{
                println("Invalid choice. Please select a valid option.")
                println("----------------------------------------------------")
            }
        }
    }
}

fun calculateTotalSalary(payrollCalculator: PayrollCalculator) {
    val header = listOf("Configurations", "Default")
    val config = listOf(
        listOf("Daily Salary", "${payrollCalculator.payrollConfig.dailyRate}"),
        listOf("Max Regular Hours", "${payrollCalculator.payrollConfig.maxHours}"),
        listOf("Workdays", "${payrollCalculator.payrollConfig.workdays}"),
        listOf("Day Type", payrollCalculator.payrollConfig.defDayType),
        listOf("IN Time", payrollCalculator.payrollConfig.defInTime),
        listOf("OUT Time", payrollCalculator.payrollConfig.defOutTime)
    )
    printTable(header, config)

    initializeDailyRecords(payrollCalculator.payrollConfig, payrollCalculator.weeklyWorkRecord)
    while (true){
        for (i in 1..7) {
            println("-----------------------------------------------")
            println("Day #$i")
            println("OUT time: ${payrollCalculator.weeklyWorkRecord.records[i - 1].outTime}")
            println("Day Type: ${payrollCalculator.weeklyWorkRecord.records[i - 1].dayType}")
        }

        println("-----------------------------------------------")
        println("Enter a day to edit. Enter 0 to continue.")
        var choice = scanner.nextInt()
        if (choice != 0) {
            editDailyWorkRecord(payrollCalculator, choice - 1)
            payrollCalculator.weeklyWorkRecord.records[choice - 1].salary = 0.0f
        }
        else
            break
    }
    payrollCalculator.calculateTotalSalary()
    var i = 1
    for (day in payrollCalculator.weeklyWorkRecord.records) {
        val formattedValue = String.format("%.2f", day.salary)
        println("Day #$i Salary = $formattedValue")
        i++
    }

    val formattedValue = String.format("%.2f", payrollCalculator.weeklyWorkRecord.totalSalary)
    println("Total Salary for the week: $formattedValue")
    payrollCalculator.weeklyWorkRecord.records.clear()
}

fun editDailyWorkRecord(payrollCalculator: PayrollCalculator, n: Int) {
    print("\nEnter OUT time (HHmm): ")
    var outTime = scanner.next()
    if (!payrollCalculator.isValidMilitaryTime(outTime)) {
        println("Invalid input. Using ${payrollCalculator.payrollConfig.defOutTime} as the default OUT time.")
        outTime = payrollCalculator.payrollConfig.defOutTime
    }

    val dayTypes = listOf("Normal", "Regular Holiday", "Special Non-Working Day")
    println("Enter day type")
    for ((index, day) in dayTypes.withIndex()) {
        println("[${index + 1}] $day")
    }
    val input = scanner.nextInt() - 1
    var selectedDayType = if (input in 0 until dayTypes.size) {
        dayTypes[input]
    } else {
        println("Invalid input. Using the default day type.")
        payrollCalculator.payrollConfig.defDayType
    }
    if (payrollCalculator.weeklyWorkRecord.records[n].isRestDay) {
        when (input) {
            0 -> selectedDayType = "Rest Day"
            1 -> selectedDayType = "Regular Holiday and Rest Day"
            2 -> selectedDayType = "Special Non-Working Day and Rest Day"
        }
    }

    val dailyWorkRecord = DailyWorkRecord(
        payrollConfig = payrollCalculator.payrollConfig,
        inTime = payrollCalculator.payrollConfig.defInTime,
        outTime = outTime,
        dayType = selectedDayType,
        regOvertimeHrs = 0F,
        nsHrs = 0F,
        salary = 0.0f,
        isAbsent = false,
        isRestDay = selectedDayType.contains("Rest Day")
    )
    payrollCalculator.weeklyWorkRecord.records[n] = dailyWorkRecord
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
    println("[7] Reset")
    println("[8] Exit to Main Menu")

    print("Enter the option number to edit (1-6): ")
    when (scanner.nextInt()) {
        1 -> {
            print("Enter new Daily Rate: ")
            val newDailyRate = scanner.nextFloat()
            payrollCalculator.payrollConfig.dailyRate = newDailyRate
            println("Daily Rate updated successfully.")
        }
        2 -> {
            var isValidInput = false
            var newMaxHours = 0
            while (!isValidInput) {
                print("Enter new Maximum Regular Hours per Day): ")
                newMaxHours = scanner.nextInt()
                if (newMaxHours in 8..24) {
                    isValidInput = true
                } else {
                    println("Invalid input. Input should be at least 8 hours.")
                }
            }
            payrollCalculator.payrollConfig.maxHours = newMaxHours
            println("Maximum Regular Hours per Day updated successfully.")
        }
        3 -> {
            var valid = false
            var newWorkdays = 0
            while (!valid) {
                print("Enter new Workdays per Week: ")
                newWorkdays = scanner.nextInt()
                if (newWorkdays in 1..7) {
                    valid = true
                } else {
                    println("Invalid input.")
                }
            }
            payrollCalculator.payrollConfig.workdays = newWorkdays
            println("Workdays per Week updated successfully.")
        }
        4 -> {
            print("Enter new Default Day Type: \n")
            var isValid = false
            var selectedDayType = ""
            while (!isValid) {
                for ((index, day) in days.withIndex()) {
                    println("[${index + 1}] $day")
                }
                val input = scanner.nextInt() - 1

                if (input in 0 until days.size) {
                    selectedDayType = days[input]
                    isValid = true
                } else {
                    println("Invalid input. Please enter a valid option.")
                }
            }
            payrollCalculator.payrollConfig.defDayType = selectedDayType
            println("Default Day Type updated successfully.")
        }
        5 -> {
            print("Enter new Default In Time (HHmm): ")
            var newInTime = scanner.next()
            payrollCalculator.payrollConfig.defInTime = newInTime
            while (!payrollCalculator.isValidMilitaryTime(newInTime)) {
                println("Invalid time format. Please enter a valid military time (HHmm).")
                print("Enter new Default In Time (HHmm): ")
                newInTime = scanner.next()
            }
            println("Default In Time updated successfully.")
        }
        6 -> {
            print("Enter new Default Out Time (HHmm): ")
            var newOutTime = scanner.next()
            payrollCalculator.payrollConfig.defOutTime = newOutTime
            while (!payrollCalculator.isValidMilitaryTime(newOutTime)) {
                println("Invalid time format. Please enter a valid military time (HHmm).")
                print("Enter new Default Out Time (HHmm): ")
                newOutTime = scanner.next()
            }
            println("Default Out Time updated successfully.")
        }
        7->{
            payrollCalculator.resetPayrollConfig()
        }
        8 -> {
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
    print("Enter OUT time (HHmm): ")
    var outTime = scanner.next()
    if (!payrollCalculator.isValidMilitaryTime(outTime)) {
        println("Invalid input. Using ${payrollCalculator.payrollConfig.defOutTime} as the default OUT time.")
        outTime = payrollCalculator.payrollConfig.defOutTime
    }
    println("Enter day type")
    for ((index, day) in days.withIndex()) {
        println("[${index+1}] $day")
    }
    val input = scanner.nextInt() - 1
    val selectedDayType = if (input in 0 until days.size) {
        days[input]
    } else {
        println("Invalid input. Using ${payrollCalculator.payrollConfig.defDayType} as the default day type.")
        payrollCalculator.payrollConfig.defDayType
    }

    val dailyWorkRecord = DailyWorkRecord(
        payrollConfig = payrollCalculator.payrollConfig,
        inTime = payrollCalculator.payrollConfig.defInTime,
        outTime = outTime,
        dayType = selectedDayType,
        regOvertimeHrs = 0F,
        nsHrs = 0F,
        salary = 0.0f,
        isAbsent = false,
        isRestDay =  false
    )

    dailyWorkRecord.salary = payrollCalculator.calculateDailySalary(dailyWorkRecord)
    val formattedValue = String.format("%.2f", dailyWorkRecord.salary)
    println("Daily Salary: $formattedValue")
}
fun displayRates() {
    val header1 = listOf("Day", "Rate")
    val premiumRates = listOf(
        listOf("Rest Day", "130%"),
        listOf("Special Non-Working Day", "130%"),
        listOf("Special Non-Working Day and Rest Day", "150%"),
        listOf("Regular Holiday", "200%"),
        listOf("Regular Holiday and Rest Day", "260%")
    )

    val header2 = listOf("Day", "Non-Night Shift", "Night Shift")
    val overtimeRates = listOf(
        listOf("Normal Day", "125%", "137.5%"),
        listOf("Rest Day", "169%", "185.9%"),
        listOf("Special Non-Working Day", "169%", "185.9%"),
        listOf("Special Non-Working Day and Rest Day", "195%", "214.5%"),
        listOf("Regular Holiday", "260%", "286.0%"),
        listOf("Regular Holiday and Rest Day", "338%", "371.8%")
    )

    printTable(header1, premiumRates)
    printTable(header2, overtimeRates)
}

fun printTable(header: List<String>, data: List<List<String>>) {
    val columnWidths = mutableListOf<Int>()
    for (i in header.indices) {
        val headerWidth = header[i].length
        val dataWidth = data.map { it[i].length }.maxOrNull() ?: 0
        columnWidths.add(maxOf(headerWidth, dataWidth))
    }
    print("+")
    for (width in columnWidths) {
        print("-".repeat(width + 2) + "+")
    }
    println()
    printRowWithBorders(header, columnWidths)
    print("+")
    for (width in columnWidths) {
        print("-".repeat(width + 2) + "+")
    }
    println()
    for (row in data) {
        printRowWithBorders(row, columnWidths)
    }
    print("+")
    for (width in columnWidths) {
        print("-".repeat(width + 2) + "+")
    }
    println()
}

fun printRowWithBorders(row: List<String>, columnWidths: List<Int>) {
    for (i in row.indices) {
        val value = row[i]
        val width = columnWidths[i]
        print("| ${value.padEnd(width)} ")
    }
    println("|")
}
