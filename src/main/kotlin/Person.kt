package org.example

import java.util.*

interface Address {
    val address: String
}

data class NationalAddress(
    override val address: String,
) : Address

data class ForeignAddress(
    override val address: String,
    val country: String,
) : Address

data class Email(
    val value: String,
)

data class Person(
    val id: UUID,
    val name: String,
    val email: Email?,
    val age: Int,
    val friends: List<Person>,
    val address: Address,
) {
    fun toDetails(): PersonDetails = PersonDetails(name, age, friends.map { it.toDetails() })
}

data class PersonDetails(
    val name: String,
    val age: Int,
    val friends: List<PersonDetails>,
)

val person1 =
    Person(
        UUID.randomUUID(),
        "Alice",
        Email("alice@example.com"),
        30,
        emptyList(),
        NationalAddress("123 Main St"),
    )
val person2 =
    Person(
        UUID.randomUUID(),
        "Bob",
        Email("bob@example.com"),
        25,
        listOf(person1),
        NationalAddress("456 Elm St"),
    )
val person3 =
    Person(
        UUID.randomUUID(),
        "Charlie",
        Email("charlie@example.com"),
        28,
        listOf(person1, person2),
        ForeignAddress("789 Oak St", "Canada"),
    )

val person2Details = person2.toDetails()
val person3Details = person3.toDetails()
