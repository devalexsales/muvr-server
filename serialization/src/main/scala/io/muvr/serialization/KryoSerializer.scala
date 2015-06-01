package io.muvr.serialization

import akka.serialization.Serializer

class KryoSerializer extends Serializer {

  override def identifier: Int = 666

  override def includeManifest: Boolean = true

  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = ???

  override def toBinary(o: AnyRef): Array[Byte] = ???

}
