# this file implements the functionalities of point groups

from point import Point

class Group(object):

    def __init__(self, points = [], radius = 0):
        self.points = points
        self.radius = radius
    
    def try_add(self, point):
        # try to add the point to the group if the distance of the point to any of the points in the group is less than twice the radius

        for p in self.points:
            if point.distance_obj(p) > 2 * self.radius:
                return False
        # attempt to add:
        self.points.append(point)

        # then see if the center of the group is still within the radius for all points
        for p in self.points:
            if self.center().distance_obj(p) > self.radius:
                self.points.remove(point)
                return False
        return True

    def center(self):
        if len(self.points) == 0:
            return Point(0, 0)
        # returns the center of the group, by taking average of all x and y coordinates
        x_sum = 0
        y_sum = 0
        for p in self.points:
            x_sum += p.x
            y_sum += p.y
        candidate = Point(int(x_sum / len(self.points) + 0.5), int(y_sum / len(self.points) + 0.5))
        # check if the candidate is within the radius for all points in the group
        return candidate
    
    def __str__(self):
        return f"Group : {len(self.points)} points, r={self.radius}, @{str(self.center())}"
    
    def __eq__(self, other):
        # if the centers are the same, then the groups are equal
        return self.center() == other.center()

    def __hash__(self) -> int:
        return hash(f"{self.center()}")

def loop_once(points, current_group):
    # add all possible points to the current_group,
    # return the updated group, and the remaining points
    remaining = []
    for p in points:
        if current_group.try_add(p):
            continue
        else:
            remaining.append(p)
    return current_group, remaining

def greedy_groups(points, radius):
    """
    given a list of points, greedily groups them into groups of points that are within the radius
    1. start from the beginning
    2. add the first point to the first group
    3. add the next point to the first group if it is within the radius, loop to the end
    4. for points that are not within the radius, add them to a new group
    4. repeat till all points are grouped
    """
    groups = []
    current_group = Group(radius=radius, points=[])
    while len(points) > 0:
        current_group, new_points = loop_once(points, current_group)
        groups.append(current_group)
        current_group = Group(radius=radius, points=[])
        points = new_points
        if len(points) == 0:
            break
    
    # remove duplicated groups
    groups = list(set(groups))
    return groups


if __name__ == '__main__':
    a = Point(1, 1)
    b = Point(2, 2)
    c = Point(3, 3)
    d = Point(4, 4)
    e = Point(5, 5)
    f = Point(6, 6)
    g = Point(7, 7)

    points = [a, b, c, d, e, f, g]
    groups = greedy_groups(points, 2)
    for g in groups:
        print(g)
