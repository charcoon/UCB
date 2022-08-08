"""Solves an instance.

Modify this file to implement your own solvers.

For usage, run `python3 solve.py --help`.
"""

import argparse
from pathlib import Path
from typing import Callable, Dict

from instance import Instance
from solution import Solution
from file_wrappers import StdinFileWrapper, StdoutFileWrapper
from groups import Group, Point, greedy_groups

# NOTE: this is just to put a tower at every needed place, without combining any.
def solve_naive(instance: Instance) -> Solution:
    return Solution(
        instance=instance,
        towers=instance.cities,
    )

# NOTE: this is to first group all the points into groups, then place a station for each group.
def solve_group(instance: Instance) -> Solution:
    # first group all the points into groups
    groups = greedy_groups(instance.cities, instance.coverage_radius)
    # then place a station for each group
    towers = []
    for group in groups:
        towers.append(group.center())
    return Solution(
        instance=instance,
        towers=towers,
    )




SOLVERS: Dict[str, Callable[[Instance], Solution]] = {
    "naive": solve_naive,
    "group": solve_group,
}


# You shouldn't need to modify anything below this line.
def infile(args):
    if args.input == "-":
        return StdinFileWrapper()

    return Path(args.input).open("r")


def outfile(args):
    if args.output == "-":
        return StdoutFileWrapper()

    return Path(args.output).open("w")


def main(args):
    with infile(args) as f:
        instance = Instance.parse(f.readlines())
        solver = SOLVERS[args.solver]
        solution = solver(instance)
        assert solution.valid(), "Solution is not valid!"
        with outfile(args) as g:
            print("# Penalty: ", solution.penalty(), file=g)
            solution.serialize(g)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Solve a problem instance.")
    parser.add_argument("input", type=str, help="The input instance file to "
                        "read an instance from. Use - for stdin.")
    parser.add_argument("--solver", required=True, type=str,
                        help="The solver type.", choices=SOLVERS.keys())
    parser.add_argument("output", type=str,
                        help="The output file. Use - for stdout.",
                        default="-")
    main(parser.parse_args())
