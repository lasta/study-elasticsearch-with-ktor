package me.lasta.studyelasticsearchwithktor.converter.zipcode

fun Zipcode.toDocument(): ZipcodeDocument = ZipcodeDocument(
    adminCode = adminCode,
    zipcode5 = zipcode5,
    zipcode = zipcode,
    prefectureRuby = prefectureRuby,
    cityRuby = cityRuby,
    townRuby = townRuby,
    prefectureName = prefectureName,
    cityName = cityName,
    townName = townName,
    representsByPluralCodes = representsByPluralCodes == 1,
    assignedStreetNumberToEachSubdivision = assignedStreetNumberToEachSubdivision == 1,
    hasCityBlock = hasCityBlock == 1,
    representsPluralTowns = representsPluralTowns == 1,
    updateStatus = updateStatus,
    updateReason = updateReason,
)
