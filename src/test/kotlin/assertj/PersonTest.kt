package assertj

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.InstanceOfAssertFactories
import org.assertj.core.api.ListAssert
import org.assertj.core.api.ObjectAssert
import org.assertj.core.api.SoftAssertions
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
        assertThat(person.age).isEqualTo(rightExpectedPersonDetails.age)
        assertThat(person.friends).isEqualTo(wrongExpectedPersonDetails.friends)
    }

    @Test
    fun `assertSoftly test person details`() {
        val person = person3
        val wrongExpectedPersonDetails = person2Details

        SoftAssertions.assertSoftly { softly ->
            softly.assertThat(person.name).isEqualTo(wrongExpectedPersonDetails.name)
            softly.assertThat(person.age).isEqualTo(wrongExpectedPersonDetails.age)
            softly.assertThat(person.friends).isEqualTo(wrongExpectedPersonDetails.friends)
        }
    }

    @Test
    fun `null narrowing test person details`() {
        val person = person3

        assertThat(person.email)
            .isNotNull()
            .extracting { it!!.value }
            .isEqualTo("dupa")
    }

    @Test
    fun `type narrowing test person details`() {
        val person = person3

        assertThat(person.address).isInstanceOf(ForeignAddress::class.java)
        val personAddress = person.address
        personAddress as ForeignAddress
        assertThat(personAddress.country).isEqualTo("Nibylandia")
    }

    @Test
    fun `testing collections`() {
        val person = person3

        assertThat(person.friends)
            .hasSize(2)
            .containsExactly(person1, person2)
        assertThat(person3Details).hasFriendsMatching(person.friends.shuffled())
    }
}

fun ObjectAssert<PersonDetails>.hasFriendsMatching(people: List<Person>) {
    (extracting { it.friends }.asInstanceOf(InstanceOfAssertFactories.LIST) as ListAssert<PersonDetails>)
        .matchesPeople(people)
}

fun ListAssert<PersonDetails>.matchesPeople(people: List<Person>): ListAssert<PersonDetails> =
    zipSatisfy(people) { actual, expected ->
        assertThat(actual).matches(expected)
    }

fun ObjectAssert<PersonDetails>.matches(person: Person): ObjectAssert<PersonDetails> =
    apply {
        extracting { it.name }.isEqualTo(person.name)
        extracting { it.age }.isEqualTo(person.age)
        extracting { it.friends }.isEqualTo(person.name)
    }
