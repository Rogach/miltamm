package org.rogach.miltamm

case class Conf(
  template: String,
  destination: String,
  keys: Seq[Key[_]]
)
