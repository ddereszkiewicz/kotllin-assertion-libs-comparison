package strikt

import org.assertj.core.api.Assertions.assertThat
import org.example.ForeignAddress
import org.example.Person
import org.example.PersonDetails
import org.example.person1
import org.example.person2
import org.example.person2Details
import org.example.person3
import org.example.person3Details
import strikt.api.Assertion
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import kotlin.test.Test

internal class PersonTest {
    @Test
    fun `standard test person details`() {
        val person = person3
        val wrongExpectedPersonDetails = person2Details
        val rightExpectedPersonDetails = person3.toDetails()

        expectThat(person).get { name }.isEqualTo(wrongExpectedPersonDetails.name)
        expectThat(person).get(Person::age).isEqualTo(rightExpectedPersonDetails.age)
    }

    @Test
    fun `assertSoftly test person details`() {
        val person = person3
        val wrongExpectedPersonDetails = person2Details

        expectThat(person) {
            get(Person::name).isEqualTo(wrongExpectedPersonDetails.name)
            get(Person::age).isEqualTo(wrongExpectedPersonDetails.age)
        }
    }

    @Test
    fun `null narrowing test person details`() {
        val person = person3

        expectThat(person.email)
            .isNotNull()
            .get { value }
            .isEqualTo("dupa")

        expectThat(person.email)
            .isNotNull()
            .and {
                get { value }
                    .isEqualTo("dupa")
            }.and {
                get { value }
                    .isEqualTo("dupa")
            }
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

        expectThat(person.friends)
            .hasSize(2)
            .containsExactly(person1, person2)
        expectThat(person3Details).hasFriendsMatching(person.friends + person)
    }
}

fun Assertion.Builder<PersonDetails>.hasFriendsMatching(people: List<Person>): Assertion.Builder<List<PersonDetails>> =
    get { friends }.matchesPeople(people)

fun Assertion.Builder<List<PersonDetails>>.matchesPeople(people: List<Person>): Assertion.Builder<List<PersonDetails>> =
    zipSatisfy(people) { actual, expected ->
        expectThat(actual).matches(expected)
    }

fun Assertion.Builder<PersonDetails>.matches(person: Person): Assertion.Builder<PersonDetails> =
    and {
        get { name }.isEqualTo(person.name)
        get { age }.isEqualTo(person.age)
    }

fun <T : Collection<E>, E, T2 : Collection<E2>, E2> Assertion.Builder<T>.zipSatisfy(
    otherElements: T2,
    satisfyFun: (E, E2) -> Unit,
) = hasSize(otherElements.count())
    .compose("every corresponding element satisfies") {
        it.zip(otherElements).forEach { (actual, expected) -> satisfyFun(actual, expected) }
    }.then {
        if (allPassed) pass() else fail()
    }
