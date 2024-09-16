package assertk

import assertk.assertions.containsExactly
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.prop
import assertk.assertions.support.expected
import org.example.Email
import org.example.ForeignAddress
import org.example.Person
import org.example.PersonDetails
import org.example.person1
import org.example.person2
import org.example.person2Details
import org.example.person3
import org.example.person3Details
import kotlin.test.Test

internal class PersonTest {
    @Test
    fun `standard test person details`() {
        val person = person3
        val wrongExpectedPersonDetails = person2Details
        val rightExpectedPersonDetails = person3.toDetails()

        assertThat(person.name).isEqualTo(rightExpectedPersonDetails.name)
        assertThat(person).prop(Person::age).isEqualTo(wrongExpectedPersonDetails.age)
        assertThat(person.friends).isEqualTo(wrongExpectedPersonDetails.friends)
    }

    @Test
    fun `assertSoftly test person details`() {
        val person = person3
        val wrongExpectedPersonDetails = person2Details

        assertThat(person).all {
            prop(Person::name).isEqualTo(wrongExpectedPersonDetails.name)
            prop(Person::age).isEqualTo(wrongExpectedPersonDetails.age)
            prop(Person::friends).isEqualTo(wrongExpectedPersonDetails.friends)
        }
    }

    @Test
    fun `null narrowing test person details`() {
        val person = person3
        // delete isNotNullAssertion –> does not compile
        assertThat(person.email)
            .isNotNull()
            .prop(Email::value)
            .isEqualTo("dupa")
    }

    @Test
    fun `type narrowing test person details`() {
        val person = person3

        // remove isInstanceOf assertion –> does not compile
        assertThat(person.address)
            .isInstanceOf(ForeignAddress::class.java)
            .prop(ForeignAddress::country)
            .isEqualTo("Nibylandia")
    }

    @Test
    fun `testing collections`() {
        val person = person3

        assertThat(person.friends)
            .hasSize(2)
        assertThat(person.friends).containsExactly(person1, person2)
        assertThat(person3Details).hasFriendsMatching(person.friends.shuffled())
    }
}

fun Assert<PersonDetails>.hasFriendsMatching(people: List<Person>) {
    prop(PersonDetails::friends).matchesPeople(people)
}

fun Assert<List<PersonDetails>>.matchesPeople(people: List<Person>): Assert<Collection<PersonDetails>> =
    zipSatisfy(people) { actual, expected ->
        assertThat(actual).matches(expected)
    }

fun Assert<PersonDetails>.matches(person: Person): Assert<PersonDetails> =
    apply {
        prop(PersonDetails::name).isEqualTo(person.name)
        prop(PersonDetails::age).isEqualTo(person.age)
    }

// No examples of writing custom assertions for collections cause this function to be kind of weird. I don't know how to do it right.
fun <T1, T2> Assert<Collection<T1>>.zipSatisfy(
    other: Collection<T2>,
    matchFunction: (T1, T2) -> Unit,
): Assert<Collection<T1>> =
    transform { actual ->
        if (actual.size != other.size) {
            expected("expected actual size: ${actual.size} to be equal to expected size: ${other.size}")
        }
        assertAll {
            actual.zip(other).forEach { (actual, expected) ->
                runCatching { matchFunction(actual, expected) }
            }
        }
        actual
    }
