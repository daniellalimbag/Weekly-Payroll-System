import java.util.*

val scanner = Scanner(System.`in`)
val days = listOf(
    "Normal",
    "Rest Day",
    "Regular Holiday",
    "Regular Holiday and Rest Day",
    "Special Non-Working Day",
    "Special Non-Working Day and Rest Day"
)

fun main() {
    val payrollConfig = PayrollConfig.Builder()
        .dailyRate(500.0f)
        .maxHours(8)
        .workdays(5)
        .defDayType("Normal")
        .defInTime("0900")
        .defOutTime("0900")
        .build()

    val weeklyWorkRecord = WeeklyWorkRecord()
    val payrollCalculator = PayrollCalculator(payrollConfig, weeklyWorkRecord)
    mainMenu(payrollCalculator)
}

fun initializeDailyRecords(payrollConfig: PayrollConfig, weeklyWorkRecord: WeeklyWorkRecord) {
    weeklyWorkRecord.records.clear()
    for (i in 0 until 7) {
        val record = if (i >= payrollConfig.workdays) {
            DailyWorkRecordFactory.createRestDayRecord(payrollConfig)
        } else {
            DailyWorkRecordFactory.createNormalRecord(payrollConfig)
        }
        weeklyWorkRecord.records.add(record)
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
            else -> println("Invalid choice. Please select a valid option.")
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

    // Initialize records using the Factory
    initializeDailyRecords(payrollCalculator.payrollConfig, payrollCalculator.weeklyWorkRecord)

    // Allow user to edit daily records
    while (true) {
        payrollCalculator.weeklyWorkRecord.records.forEachIndexed { index, record ->
            println("-----------------------------------------------")
            println("Day #${index + 1}")
            println("OUT time: ${record.outTime}")
            println("Day Type: ${record.dayType}")
        }

        println("-----------------------------------------------")
        println("Enter a day to edit. Enter 0 to continue.")
        val choice = scanner.nextInt()
        if (choice != 0) {
            editDailyWorkRecord(payrollCalculator, choice - 1)
        } else break
    }

    payrollCalculator.calculateTotalSalary()
    payrollCalculator.weeklyWorkRecord.records.forEachIndexed { index, record ->
        val formattedValue = String.format("%.2f", record.salary)
        println("Day #${index + 1} Salary = $formattedValue")
    }

    val totalFormattedValue = String.format("%.2f", payrollCalculator.weeklyWorkRecord.totalSalary)
    println("Total Salary for the week: $totalFormattedValue")
}

fun editDailyWorkRecord(payrollCalculator: PayrollCalculator, n: Int) {
    print("\nEnter OUT time (HHmm): ")
    var outTime = scanner.next()
    if (!payrollCalculator.isValidMilitaryTime(outTime)) {
        println("Invalid input. Using ${payrollCalculator.payrollConfig.defOutTime} as the default OUT time.")
        outTime = payrollCalculator.payrollConfig.defOutTime
    }

    println("Enter day type:")
    days.forEachIndexed { index, day -> println("[${index + 1}] $day") }

    val input = scanner.nextInt() - 1
    val updatedRecord = when (input) {
        0 -> DailyWorkRecordFactory.createNormalRecord(payrollCalculator.payrollConfig)
        1 -> DailyWorkRecordFactory.createRestDayRecord(payrollCalculator.payrollConfig)
        2 -> DailyWorkRecordFactory.createRegularHolidayRecord(payrollCalculator.payrollConfig)
        3 -> DailyWorkRecordFactory.createRegularHolidayRestDayRecord(payrollCalculator.payrollConfig)
        4 -> DailyWorkRecordFactory.createSpecialNonWorkingDayRecord(payrollCalculator.payrollConfig)
        5 -> DailyWorkRecordFactory.createSpecialNonWorkingRestDayRecord(payrollCalculator.payrollConfig)
        else -> {
            println("Invalid input. No changes made.")
            return
        }
    }.also {
        it.outTime = outTime
    }

    payrollCalculator.weeklyWorkRecord.records[n] = updatedRecord
    println("Day #${n + 1} updated successfully.")
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
    println("[7] Reset to Default Configurations")
    println("[8] Exit to Main Menu")

    print("Enter the option number to edit: ")
    val builder = PayrollConfig.Builder()
        .dailyRate(payrollCalculator.payrollConfig.dailyRate)
        .maxHours(payrollCalculator.payrollConfig.maxHours)
        .workdays(payrollCalculator.payrollConfig.workdays)
        .defDayType(payrollCalculator.payrollConfig.defDayType)
        .defInTime(payrollCalculator.payrollConfig.defInTime)
        .defOutTime(payrollCalculator.payrollConfig.defOutTime)

    when (scanner.nextInt()) {
        1 -> {
            print("Enter new Daily Rate: ")
            val newDailyRate = scanner.nextFloat()
            payrollCalculator.payrollConfig = builder.dailyRate(newDailyRate).build()
            println("Daily Rate updated successfully.")
        }
        2 -> {
            print("Enter new Maximum Regular Hours per Day (8-24): ")
            val newMaxHours = scanner.nextInt().coerceIn(8, 24) // Validating input
            payrollCalculator.payrollConfig = builder.maxHours(newMaxHours).build()
            println("Maximum Regular Hours per Day updated successfully.")
        }
        3 -> {
            print("Enter new Workdays per Week (1-7): ")
            val newWorkdays = scanner.nextInt().coerceIn(1, 7) // Validating input
            payrollCalculator.payrollConfig = builder.workdays(newWorkdays).build()
            println("Workdays per Week updated successfully.")
        }
        4 -> {
            println("Select new Default Day Type:")
            days.forEachIndexed { index, day -> println("[${index + 1}] $day") }
            val input = scanner.nextInt() - 1
            if (input in days.indices) {
                payrollCalculator.payrollConfig = builder.defDayType(days[input]).build()
                println("Default Day Type updated successfully.")
            } else {
                println("Invalid input. No changes made.")
            }
        }
        5 -> {
            print("Enter new Default In Time (HHmm): ")
            var newInTime = scanner.next()
            while (!payrollCalculator.isValidMilitaryTime(newInTime)) {
                println("Invalid time format. Please enter a valid military time (HHmm).")
                print("Enter new Default In Time (HHmm): ")
                newInTime = scanner.next()
            }
            payrollCalculator.payrollConfig = builder.defInTime(newInTime).build()
            println("Default In Time updated successfully.")
        }
        6 -> {
            print("Enter new Default Out Time (HHmm): ")
            var newOutTime = scanner.next()
            while (!payrollCalculator.isValidMilitaryTime(newOutTime)) {
                println("Invalid time format. Please enter a valid military time (HHmm).")
                print("Enter new Default Out Time (HHmm): ")
                newOutTime = scanner.next()
            }
            payrollCalculator.payrollConfig = builder.defOutTime(newOutTime).build()
            println("Default Out Time updated successfully.")
        }
        7 -> {
            payrollCalculator.resetPayrollConfig()
            println("Configurations reset to default values.")
        }
        8 -> {
            println("Returning to the Main Menu.")
            return
        }
        else -> println("Invalid option. Please enter a valid option.")
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

    println("Enter day type:")
    days.forEachIndexed { index, day -> println("[${index + 1}] $day") }

    val input = scanner.nextInt() - 1
    val selectedDayType = if (input in days.indices) {
        days[input]
    } else {
        println("Invalid input. Using the default day type.")
        payrollCalculator.payrollConfig.defDayType
    }

    // Build a daily work record
    val dailyWorkRecord = DailyWorkRecordBuilder(payrollCalculator.payrollConfig)
        .setInTime(payrollCalculator.payrollConfig.defInTime)
        .setOutTime(outTime)
        .setDayType(selectedDayType)
        .setIsRestDay(selectedDayType.contains("Rest Day"))
        .calculateShiftData()
        .build()

    // Calculate salary
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
