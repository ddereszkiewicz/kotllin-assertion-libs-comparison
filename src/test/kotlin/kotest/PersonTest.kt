package kotest

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldMatchEach
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
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

        person.name shouldBe rightExpectedPersonDetails.name
        person.age shouldBe rightExpectedPersonDetails.age
        person.friends shouldBe wrongExpectedPersonDetails.friends
    }

    @Test
    fun `assertSoftly test person details`() {
        val person = person3
        val wrongExpectedPersonDetails = person2Details

        assertSoftly(person) {
            name shouldBe wrongExpectedPersonDetails.name
            age shouldBe wrongExpectedPersonDetails.age
            friends shouldBe wrongExpectedPersonDetails.friends
        }
    }

    @Test
    fun `null narrowing test person details`() {
        val person = person3

        person.email
            .shouldNotBeNull()
            .value
            .shouldBe("dupa")
    }

    @Test
    fun `type narrowing test person details`() {
        val person = person3

        person.address
            .shouldBeInstanceOf<ForeignAddress>()
            .country
            .shouldBe("Nibylandia")
    }

    @Test
    fun `testing collections`() {
        val person = person3

        person.friends
            .shouldHaveSize(2)
            .shouldContainExactly(person1, person2)
        person3Details.shouldHaveFriendsMatching(person.friends.shuffled())
    }
}

fun PersonDetails.shouldHaveFriendsMatching(people: List<Person>) {
    friends.shouldMatchPeople(people)
}

fun List<PersonDetails>.shouldMatchPeople(people: List<Person>): List<PersonDetails> =
    shouldZipSatisfy(people) { actual, expected ->
        actual.shouldMatch(expected)
    }

fun PersonDetails.shouldMatch(person: Person): PersonDetails =
    assertSoftly(this) {
        name shouldBe person.name
        age shouldBe person.age
    }

private fun <T, T2> List<T>.shouldZipSatisfy(
    other: List<T2>,
    satisfyFun: (T, T2) -> Unit,
): List<T> {
    shouldMatchEach(other.map { otherElement -> { element -> satisfyFun(element, otherElement) } })
    return this
}
