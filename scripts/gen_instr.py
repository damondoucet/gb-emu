"""
    Generate an instruction class shell.

    Arguments:
        Mnemonic Class [<arg 1 type> <arg 1 name> [<arg 2 type> <arg 2 name> [...]]]

        The names should be in lowerCamelCase.

    Example:
        SET Set8 int bitIndex Register8 r8
"""

import argparse

NO_ARGS_FORMAT = """public static class %sInstruction implements Instruction {
    @Override
    public boolean equals(Object rhs) {
        return rhs != null && getClass() == rhs.getClass();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "%s";
    }

    @Override
    public void execute(EmulatorState state) {

    }
}"""


ARGS_FORMAT = """public static class %sInstruction implements Instruction {
    %s

    public %sInstruction(%s) {
        %s
    }

    @Override
    public boolean equals(Object rhs) {
        if (rhs == null || getClass() != rhs.getClass())
            return false;
        %sInstruction other = (%sInstruction)rhs;
        return %s;
    }

    @Override
    public int hashCode() {
        return Objects.hash(%s);
    }

    @Override
    public String toString() {
        return String.format("%s %s", %s);
    }

    @Override
    public void execute(EmulatorState state) {

    }
}"""

class Argument(object):
    def __init__(self, type_str, lower_camel_name_str):
        self.type_str = type_str
        self.lower_camel_name_str = lower_camel_name_str

    def arg_str(self):
        return "%s %s" % (self.type_str, self.lower_camel_name_str)

    def field_name(self):
        return "_" + self.lower_camel_name_str

    def field_str(self):
        return "private final %s %s;" % (
            self.type_str, self.field_name())

    def is_int(self):
        return self.type_str == "int"

    # Which format specifier to use for toString()
    def format_specifier(self):
        return "%d" if self.is_int() else "%s"

    def to_string_value(self):
        ret = self.field_name()

        if not self.is_int():
            ret += ".toString()"

        return ret

def create_init_str(arguments):
    return "\n        ".join([
        "%s = %s;" % (arg.field_name(), arg.lower_camel_name_str)
            for arg in arguments])

def create_equals_str(arguments):
    return " &&\n               ".join([
        "%s == other.%s" % (arg.field_name(), arg.field_name())
            for arg in arguments])

def create_class_string_with_args(mnemonic, class_name, arguments):
    field_strs = "\n    ".join([arg.field_str() for arg in arguments])
    field_names_as_args = ", ".join([arg.field_name() for arg in arguments])
    arg_str_with_types = ", ".join([arg.arg_str() for arg in arguments])

    init_str = create_init_str(arguments)
    equals_str = create_equals_str(arguments)

    to_string_formats = ", ".join([arg.format_specifier() for arg in arguments])
    to_string_values = ", ".join([arg.to_string_value() for arg in arguments])

    return ARGS_FORMAT % (
        class_name,
        field_strs,
        class_name,
        arg_str_with_types,
        init_str,
        class_name,
        class_name,
        equals_str,
        field_names_as_args,
        mnemonic,
        to_string_formats,
        to_string_values)

def create_class_without_args(mnemonic, class_name):
    return NO_ARGS_FORMAT % (class_name, mnemonic)

def create_class_string(mnemonic, class_name, arguments):
    return create_class_string_with_args(mnemonic, class_name, arguments) \
        if len(arguments) > 0 else create_class_without_args(mnemonic, class_name)

# strs will be a list where even-index elements are types and odd-index
# elements are lowerCamelCase names. There must be an even number of them.
# Returns a list of Argument objects.
def strs_to_arguments(strs):
    if not strs:
        return []

    if len(strs) % 2 != 0:
        raise Exception("Missing type or field name in arguments list (%s)" % \
            ", ".join(strs))

    return [Argument(strs[i * 2], strs[i * 2 + 1])
        for i in xrange(len(strs) / 2)]

def main():
    parser = argparse.ArgumentParser(
        description="Generate an instruction class shell")

    parser.add_argument("mnemonic",
        help="Mnemonic for the instruction")
    parser.add_argument("class_name",
        help="Class name (without Instruction)")

    parser.add_argument("arguments", nargs="*",
        help="Pairs of type and lowerCamelCase names, space-separated")

    args = parser.parse_args()
    print create_class_string(
        args.mnemonic, args.class_name, strs_to_arguments(args.arguments))

if __name__ == '__main__':
    main()
