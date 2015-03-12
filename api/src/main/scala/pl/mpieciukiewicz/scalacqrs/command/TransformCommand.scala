package pl.mpieciukiewicz.scalacqrs.command

trait TransformCommand { self: Command[_] =>

  def transform(): Command[_]

}
