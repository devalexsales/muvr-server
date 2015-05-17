package io.muvr

trait UserMessage {
  /**
   * Return the user identity
   * @return the user id
   */
  def userId: UserId

  /**
   * Returns the shard region for the given user message
   * @return the shard region
   */
  def shardRegion(): String = s"${userId.hashCode() % 10}"
}
