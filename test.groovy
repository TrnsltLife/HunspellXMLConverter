def file = "test.dic"
def test = (file ==~ /.*?\.(aff|dic)/)
println test