import java.util.*
val scanner = Scanner(System.`in`)
val days = listOf("Normal", "Rest Day", "Regular Holiday", "Special Non-Working Day", "Special Non-Working Day and Rest Day")

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
        listOf("Day Type", "${payrollCalculator.payrollConfig.defDayType}"),
        listOf("IN Time", "${payrollCalculator.payrollConfig.defInTime}"),
        listOf("OUT Time", "${payrollCalculator.payrollConfig.defOutTime}")
    )
    printTable(header, config)
    payrollCalculator.calculateTotalSalary()
    for (i in 1..7) {
        println("----------------------------------------------------")
        print("[$i] ")
        addDailyWorkRecord(payrollCalculator)
        println("----------------------------------------------------")
    }
    payrollCalculator.calculateTotalSalary()

    var i = 1
    for (day in payrollCalculator.weeklyWorkRecord.records) {
        println("Day #$i Salary = ${day.salary}")
        i++
    }

    println("Total Salary for the week: ${payrollCalculator.weeklyWorkRecord.totalSalary}")
    payrollCalculator.weeklyWorkRecord.records.clear()
}

fun addDailyWorkRecord(payrollCalculator: PayrollCalculator) {
    print("[Add Daily Work Record]")
    print("\nEnter OUT time (HHmm): ")
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
        if(payrollCalculator.weeklyWorkRecord.records.size == payrollCalculator.payrollConfig.workdays){
            println("Invalid input. Using Rest Day as the default day type.")
            days[1]
        }
        else {
            println("Invalid input. Using ${payrollCalculator.payrollConfig.defDayType} as the default day type.")
            payrollCalculator.payrollConfig.defDayType
        }
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
            var isValidInput = false
            var newMaxHours = 0
            while (!isValidInput) {
                print("Enter new Maximum Regular Hours per Day): ")
                newMaxHours = scanner.nextInt()
                if (newMaxHours in 1..24) {
                    isValidInput = true
                } else {
                    println("Invalid input.")
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
        regOvertimeHrs = 0,
        nsHrs = 0,
        salary = 0.0f,
        isAbsent = false
    )

    dailyWorkRecord.salary = payrollCalculator.calculateDailySalary(dailyWorkRecord)
    println("Daily Salary: ${dailyWorkRecord.salary}")
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

    // Find maximum column widths
    for (i in header.indices) {
        val headerWidth = header[i].length
        val dataWidth = data.map { it[i].length }.maxOrNull() ?: 0
        columnWidths.add(maxOf(headerWidth, dataWidth))
    }

    // Print top border
    print("+")
    for (width in columnWidths) {
        print("-".repeat(width + 2) + "+")
    }
    println()

    // Print header
    printRowWithBorders(header, columnWidths)

    // Print middle border
    print("+")
    for (width in columnWidths) {
        print("-".repeat(width + 2) + "+")
    }
    println()

    // Print data rows
    for (row in data) {
        printRowWithBorders(row, columnWidths)
    }

    // Print bottom border
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
