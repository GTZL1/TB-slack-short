open class PlayCard(val name: String, var owner: String, val id: Int) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PlayCard) return false

        if (name != other.name) return false
        if (owner != other.owner) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + owner.hashCode()
        result = 31 * result + id
        return result
    }
}