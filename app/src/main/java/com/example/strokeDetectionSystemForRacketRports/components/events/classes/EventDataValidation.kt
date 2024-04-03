package com.example.strokeDetectionSystemForRacketRports.components.events.classes

import javax.inject.Inject

/**
 * This class contains the functionality related to validate all the data of an event.
 *
 */
class EventDataValidation @Inject constructor() {
    /**
     * Allows to validate the event name.
     *
     */
    fun isValidEventName(eventName: String): Boolean {
        return eventName.isNotEmpty()
    }

    /**
     * Allows to validate the event address.
     *
     */
    fun isValidEventAdress(eventAdress: String): Boolean {
        return eventAdress.isNotEmpty()
    }

    /**
     * Allows to validate the event date.
     *
     */
    fun isValidEventDate(eventDate: String): Boolean {
        return eventDate.isNotEmpty()
    }

    /**
     * Allows to validate the event time.
     *
     */
    fun isValidEventTime(eventTime: String): Boolean {
        return eventTime.isNotEmpty()
    }

    /**
     * Allows to validate the event court type.
     *
     */
    fun isValidEventCoutType(courtTypeName: String): Boolean {
        return courtTypeName.isNotEmpty()
    }

    /**
     * Allows to validate the event players number.
     *
     */
    fun isValidEventPlayersNumber(playersNumberName: String): Boolean {
        return playersNumberName.isNotEmpty()
    }

    /**
     * Allows to validate the event description.
     *
     */
    fun isValidEventDescription(description: String): Boolean {
        return description.isNotEmpty()
    }

    /**
     * Allows to validate the event match result.
     *
     */
    fun isValidResult(result: MutableList<String>): Boolean{
        return result.get(0)!="0" &&
                isValidSetNumber(result.get(1).toInt()) &&
                isValidSetNumber(result.get(2).toInt()) &&
                isValidSetNumber(result.get(3).toInt()) &&
                isValidSetNumber(result.get(4).toInt()) &&
                isValidSetNumber(result.get(5).toInt()) &&
                isValidSetNumber(result.get(6).toInt()) &&
                isValidSetNumber(result.get(7).toInt()) &&
                isValidSetNumber(result.get(8).toInt()) &&
                isValidSetNumber(result.get(9).toInt()) &&
                isValidSetNumber(result.get(10).toInt())
    }

    /**
     * Allows to validate a match set.
     *
     */
    private fun isValidSetNumber(number: Int): Boolean{
        return number in 0..7
    }
}