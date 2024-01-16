import re

debug = False

def cmp(x,y):
    return (x > y) - (x < y)


class VersionComparator:
        # compare debian version numbers in the format
        # [epoch:]upstream_version[-debian_revision] 

        re_number = re.compile("(\d+)")
        re_notnumber = re.compile("(\D+)")
        re_epoch = re.compile("(\d+):(.+)")
        re_parts = re.compile("(.+)-(.+)")

        def compare_part(self,a,b):
                if (a==None or a=='') and (b==None or b==''):
                        return 0

                match_a = self.re_notnumber.match(a)
                match_b = self.re_notnumber.match(b)
                if match_a:
                        nondigits_a = match_a.group(1)
                else:
                        nondigits_a = ""

                if match_b:
                        nondigits_b = match_b.group(1)
                else:
                        nondigits_b = ""

                a=a[len(nondigits_a):]
                b=b[len(nondigits_b):]
                comp=self.compare_nondigits(nondigits_a,nondigits_b)
                if debug:
                        print("compare '%s' to '%s': %d" % (nondigits_a,nondigits_b,comp))
                        print("rest:",a,b)
                if comp!=0:
                        return comp

                match_a = self.re_number.match(a)
                match_b = self.re_number.match(b)
                if match_a:
                        digits_a = match_a.group(1)
                else:
                        digits_a = "0"
                if match_b:
                        digits_b = match_b.group(1)
                else:
                        digits_b = "0"

                a=a[len(digits_a):]
                b=b[len(digits_b):]
                comp=cmp(int(digits_a),int(digits_b))
                if debug:
                        print("compare %s to %s: %d" % (digits_a,digits_b,comp))
                        print("rest:",a,b)
                if comp!=0:
                        return comp

                return self.compare_part(a,b) 

        def compare_nondigits(self,a,b):
                i=0
                length_a=len(a)
                length_b=len(b)
                while i<length_a or i<length_b:
                        if i<length_a:
                                char_a=a[i]
                        else:
                                char_a=None

                        if i<length_b:
                                char_b=b[i]
                        else:
                                char_b=None
                        # print i,char_a,char_b
                        r=self.compare_character(char_a,char_b)
                        i=i+1
                        if r!=0:
                                return r
                if length_a>length_b:
                        return 1
                if length_a<length_b:
                        return -1
                return 0

        # sort letters before non-letters, empty string before letters,
        # ~ before everything else
        #
        def char_to_ord(self,a):
                if(a=='~'):
                        return -1
                if(a==''or a==None):
                        return 0
                if not a.isalpha():
                        return ord(a)+256
                return ord(a)

        def compare_character(self,a,b):
                x=self.char_to_ord(a)
                y=self.char_to_ord(b)
                if x<y: return -1
                if x>y: return 1
                return 0

        def separate_epoch(self,s):
                match=self.re_epoch.match(s)
                if not match:
                        return (s,0)
                epoch=match.group(1)
                return (match.group(2),int(epoch))

        def compare(self,a,b):
                a,epoch_a=self.separate_epoch(a)
                b,epoch_b=self.separate_epoch(b)

                if epoch_a>epoch_b:
                        return 1
                if epoch_a<epoch_b:
                        return -1

                upstream_a=None
                upstream_b=None
                epoch_a=None
                epoch_b=None
                revision_a=None
                revision_b=None

                match_a=self.re_parts.match(a)
                match_b=self.re_parts.match(b)

                if match_a:
                        upstream_a=match_a.group(1)
                        revision_a=match_a.group(2)
                else:
                        upstream_a=a
                        revision_a=''

                if match_b:
                        upstream_b=match_b.group(1)
                        revision_b=match_b.group(2)
                else:
                        upstream_b=b
                        revision_b=''

                comp=self.compare_part(upstream_a,upstream_b)
                if comp!=0:
                        return comp

                return self.compare_part(revision_a,revision_b)

