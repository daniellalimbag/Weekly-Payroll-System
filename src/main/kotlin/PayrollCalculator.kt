import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.math.abs
import kotlin.math.max

class PayrollCalculator(
    var payrollConfig: PayrollConfig = PayrollConfig(),
    var weeklyWorkRecord: WeeklyWorkRecord
){
    fun isValidMilitaryTime(time: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("HHmm")

        return try {
            // Attempt to parse the time string
            LocalTime.parse(time, formatter)
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }
    fun resetPayrollConfig() {
        payrollConfig = PayrollConfig()
    }
    fun calculateTotalSalary() {
        weeklyWorkRecord.totalSalary = 0.0f

        for (record in weeklyWorkRecord.records) {
            val salary = calculateDailySalary(record)
            record.salary = salary
            weeklyWorkRecord.totalSalary += salary
        }
    }

    fun calculateDailySalary(record: DailyWorkRecord): Float{
        val hourlyRate = payrollConfig.dailyRate / payrollConfig.maxHours
        var Salary = 0.0F
        if (!record.isAbsent){
            if (record.dayType != "Normal" || record.isNightShift) {
                Salary = calculatePremiumPay(record, hourlyRate)
            } else {
                Salary = payrollConfig.dailyRate
            }

            if(record.regOvertimeHrs > 0 || record.nsHrs > 0){
                Salary += calculateOvertimePay(record, hourlyRate)
            }
        }
        return Salary
    }

    private fun calculatePremiumPay(record: DailyWorkRecord, hourlyRate: Float): Float {
        val payRates = mapOf(
            Pair("Normal", 1.0f),
            Pair("Rest Day", 1.3f), //130% for rest days
            Pair("Special Non-Working Day", 1.3f), //130% for special non-working days
            Pair("Special Non-Working Day and Rest Day", 1.5f), //150% for whatever this is
            Pair("Regular Holiday", 2.0f), //200% for regular holidays
            Pair("Regular Holiday and Rest Day", 2.6f) //260% for this combination
        )
        var pay = 0.0f
        if (record.dayType in payRates) {
            val dayRate = record.payrollConfig.maxHours * hourlyRate * payRates[record.dayType]!!
            pay += dayRate
            if (record.isNightShift) {
                // Apply the 10% night shift differential
                pay += (record.nsHrs * hourlyRate * 1.10).toFloat()
            }
        }
        return pay
    }
    private fun calculateOvertimePay(record: DailyWorkRecord, hourlyRate: Float): Float{
        val overtimeRates = mapOf(
            Pair("Normal", 1.25f), //125% for overtime on normal days
            Pair("Rest Day", 1.69f), //169% for overtime on rest days
            Pair("Special Non-Working Day", 1.69f), //169% for overtime on special non-working days
            Pair("Special Non-Working Day and Rest Day", 1.95f), //195% for overtime on this
            Pair("Regular Holiday", 2.6f) //260% for overtime on regular holidays
        )
        val nightShiftRates = mapOf(
            Pair("Normal", 1.375f), //137.5% for overtime on normal days
            Pair("Rest Day", 1.859f), //185.9% for overtime on rest days
            Pair("Special Non-Working Day", 1.859f), //185.9% for overtime on special non-working days
            Pair("Special Non-Working Day and Rest Day", 2.145f), //214.5% for overtime on this again
            Pair("Regular Holiday", 2.86f) //286% for overtime on regular holidays
        )
        var overtimePay = 0.0f

        if(record.nsOvertimeHrs > 0){
            if (record.dayType in nightShiftRates) {
                overtimePay += record.nsOvertimeHrs * hourlyRate * nightShiftRates[record.dayType]!!
            }
        }

        if(record.regOvertimeHrs > 0){
            if (record.dayType in overtimeRates) {
                overtimePay += record.regOvertimeHrs * hourlyRate * overtimeRates[record.dayType]!!
            }
        }
        return overtimePay
    }
}
