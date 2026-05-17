package com.madhum.marga.logic

/**
 * FloraData — Static Indian flora blooming calendar.
 * FR-07: Flora Calendar — display nearby blooming flowers by month/season.
 * Contains 12 months of blooming data for Indian beekeeping regions.
 */
object FloraData {

    data class FloraEntry(
        val month: String,
        val monthNumber: Int,
        val season: String,
        val flowers: List<FlowerInfo>,
        val honeyFlowRating: Int,   // 1–5 stars
        val beekeeperTip: String
    )

    data class FlowerInfo(
        val name: String,
        val localName: String,
        val honeyYield: String,     // "High" / "Medium" / "Low"
        val color: String,
        val region: String
    )

    val calendar: List<FloraEntry> = listOf(
        FloraEntry(
            month = "January",
            monthNumber = 1,
            season = "Winter",
            honeyFlowRating = 5,
            beekeeperTip = "Peak mustard season! Place hives near mustard fields. Excellent honey yield. Add supers.",
            flowers = listOf(
                FlowerInfo("Mustard", "Sarson", "High", "#F5E642", "North India, Punjab, Haryana"),
                FlowerInfo("Sunflower", "Surajmukhi", "High", "#F59E0B", "Karnataka, Maharashtra"),
                FlowerInfo("Coriander", "Dhaniya", "Medium", "#FFFFFF", "Rajasthan, Gujarat")
            )
        ),
        FloraEntry(
            month = "February",
            monthNumber = 2,
            season = "Spring",
            honeyFlowRating = 5,
            beekeeperTip = "Excellent foraging month. Litchi and mango blossom produce premium honey. Expand colonies.",
            flowers = listOf(
                FlowerInfo("Litchi", "Lychee", "High", "#FFB6C1", "Bihar, Uttarakhand"),
                FlowerInfo("Mango Blossom", "Aam", "High", "#FDE68A", "All India"),
                FlowerInfo("Mustard", "Sarson", "High", "#F5E642", "North India"),
                FlowerInfo("Calendula", "Genda Phool", "Medium", "#F59E0B", "Hills, gardens")
            )
        ),
        FloraEntry(
            month = "March",
            monthNumber = 3,
            season = "Spring",
            honeyFlowRating = 5,
            beekeeperTip = "Spring peak — highest honey production period. Monitor for swarming. Check queen space.",
            flowers = listOf(
                FlowerInfo("Litchi", "Lychee", "High", "#FFB6C1", "Bihar, Uttarakhand"),
                FlowerInfo("Jamun", "Black Plum", "Medium", "#4C1D95", "Central India"),
                FlowerInfo("Neem", "Neem", "Medium", "#16A34A", "All India"),
                FlowerInfo("Sesame", "Til", "Medium", "#F3F4F6", "Rajasthan, Gujarat")
            )
        ),
        FloraEntry(
            month = "April",
            monthNumber = 4,
            season = "Summer",
            honeyFlowRating = 3,
            beekeeperTip = "Summer transition. Eucalyptus provides moderate flow. Ensure water sources near hives.",
            flowers = listOf(
                FlowerInfo("Eucalyptus", "Nilgiri", "Medium", "#FFFFFF", "South India, plantations"),
                FlowerInfo("Jamun", "Black Plum", "Medium", "#4C1D95", "Central India"),
                FlowerInfo("Siris", "Shirish", "Low", "#FDE68A", "North India")
            )
        ),
        FloraEntry(
            month = "May",
            monthNumber = 5,
            season = "Summer",
            honeyFlowRating = 2,
            beekeeperTip = "Heat stress month. Reduce colony size if needed. Supplement feed if natural forage is scarce.",
            flowers = listOf(
                FlowerInfo("Tulsi", "Holy Basil", "Low", "#FFFFFF", "All India"),
                FlowerInfo("Custard Apple", "Sitaphal", "Low", "#16A34A", "Deccan Plateau"),
                FlowerInfo("Maize Pollen", "Makka", "Low", "#F59E0B", "Agricultural fields")
            )
        ),
        FloraEntry(
            month = "June",
            monthNumber = 6,
            season = "Monsoon",
            honeyFlowRating = 1,
            beekeeperTip = "Pre-monsoon. Very limited forage. Provide sugar syrup supplementation. Protect hives from rain.",
            flowers = listOf(
                FlowerInfo("Karanj", "Pongam", "Low", "#D97706", "Coastal, river banks"),
                FlowerInfo("Moringa", "Drumstick", "Low", "#FFFFFF", "South India")
            )
        ),
        FloraEntry(
            month = "July",
            monthNumber = 7,
            season = "Monsoon",
            honeyFlowRating = 1,
            beekeeperTip = "Full monsoon — lowest honey flow. Maintain hive population. Treat varroa if needed.",
            flowers = listOf(
                FlowerInfo("Coconut", "Nariyal", "Low", "#FFFFFF", "Kerala, coastal areas"),
                FlowerInfo("Ginger Flower", "Adrak", "Low", "#F59E0B", "Northeast India")
            )
        ),
        FloraEntry(
            month = "August",
            monthNumber = 8,
            season = "Monsoon",
            honeyFlowRating = 2,
            beekeeperTip = "Late monsoon — flow improving. Prepare hives for post-monsoon build-up.",
            flowers = listOf(
                FlowerInfo("Soybean", "Soya", "Low", "#FDE68A", "Madhya Pradesh, Maharashtra"),
                FlowerInfo("Marigold", "Genda", "Low", "#F59E0B", "All India")
            )
        ),
        FloraEntry(
            month = "September",
            monthNumber = 9,
            season = "Autumn",
            honeyFlowRating = 3,
            beekeeperTip = "Post-monsoon recovery begins. Colony build-up phase. Inspect hives after monsoon break.",
            flowers = listOf(
                FlowerInfo("Sesame", "Til", "Medium", "#F3F4F6", "South & Central India"),
                FlowerInfo("Sunflower", "Surajmukhi", "Medium", "#F59E0B", "Karnataka, AP"),
                FlowerInfo("Cotton", "Kapas", "Medium", "#FFFFFF", "Maharashtra, Gujarat")
            )
        ),
        FloraEntry(
            month = "October",
            monthNumber = 10,
            season = "Autumn",
            honeyFlowRating = 4,
            beekeeperTip = "Good flow from sunflower and cotton. Strengthen colonies for winter peak. Check for varroa.",
            flowers = listOf(
                FlowerInfo("Sunflower", "Surajmukhi", "High", "#F59E0B", "All India"),
                FlowerInfo("Mustard (early)", "Sarson", "Medium", "#F5E642", "North India"),
                FlowerInfo("Safflower", "Kusum", "Medium", "#EF4444", "Maharashtra, Karnataka")
            )
        ),
        FloraEntry(
            month = "November",
            monthNumber = 11,
            season = "Winter",
            honeyFlowRating = 5,
            beekeeperTip = "Peak winter flow starts! Mustard in full bloom. Add supers. Great time for honey harvest.",
            flowers = listOf(
                FlowerInfo("Mustard", "Sarson", "High", "#F5E642", "North India, Punjab"),
                FlowerInfo("Sunflower", "Surajmukhi", "High", "#F59E0B", "Karnataka, AP"),
                FlowerInfo("Coriander", "Dhaniya", "Medium", "#FFFFFF", "Rajasthan"),
                FlowerInfo("Fennel", "Saunf", "Medium", "#16A34A", "Gujarat, Rajasthan")
            )
        ),
        FloraEntry(
            month = "December",
            monthNumber = 12,
            season = "Winter",
            honeyFlowRating = 5,
            beekeeperTip = "Excellent honey production month. Harvest light-colored, high-quality mustard honey.",
            flowers = listOf(
                FlowerInfo("Mustard", "Sarson", "High", "#F5E642", "North India"),
                FlowerInfo("Eucalyptus", "Nilgiri", "Medium", "#FFFFFF", "South India"),
                FlowerInfo("Sunflower", "Surajmukhi", "High", "#F59E0B", "South India"),
                FlowerInfo("Borage", "Gaozaban", "Medium", "#93C5FD", "Himachal Pradesh")
            )
        )
    )

    fun getCurrentMonthFlora(): FloraEntry {
        val month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1
        return calendar.first { it.monthNumber == month }
    }

    fun getFlowerCountRating(count: Int): String = when {
        count >= 4 -> "⭐⭐⭐⭐⭐"
        count == 3 -> "⭐⭐⭐⭐"
        count == 2 -> "⭐⭐⭐"
        else -> "⭐⭐"
    }
}
