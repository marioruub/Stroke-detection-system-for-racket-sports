package com.example.strokeDetectionSystemForRacketRports.components.strokes.model

/**
 * This data class contains only the name of the stroke.
 *
 */
data class Stroke(var name: String)

/**
 * This data class contains the attributes related to the number of strokes from each type.
 *
 */
data class Strokes(var forehand: Int,
                   var backhand: Int,
                   var serve: Int
)