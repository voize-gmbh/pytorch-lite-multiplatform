from typing import Dict, List, NamedTuple, Tuple

import torch
import torch.nn as nn

@torch.jit.export
class State(NamedTuple):
    a: int
    b: int

class DummyModel(nn.Module):
    def __init__(self):
        super().__init__()
        self.linear = nn.Linear(10, 10)
        self.cos_sim = nn.CosineSimilarity()

    @torch.jit.export
    def inference(self, x: torch.Tensor):
        return self.linear(x)

    @torch.jit.export
    def inference_dict_string(self, x: Dict[str, torch.Tensor]):
        return self.linear(x["x"])

    @torch.jit.export
    def inference_dict_long(self, x: Dict[int, torch.Tensor]):
        return self.linear(x[0])

    @torch.jit.export
    def identity_tensor(self, x: torch.Tensor):
        return x

    @torch.jit.export
    def identity_long(self, x: int):
        return x

    @torch.jit.export
    def identity_bool(self, x: bool):
        return x

    @torch.jit.export
    def identity_list(self, x: List[int]):
        return x

    @torch.jit.export
    def identity_bool_list(self, x: List[bool]):
        return x

    @torch.jit.export
    def identity_tuple(self, x: Tuple[int]):
        return x

    @torch.jit.export
    def identity_dict_string(self, x: Dict[str, int]):
        return x

    @torch.jit.export
    def identity_dict_long(self, x: Dict[int, int]):
        return x

    @torch.jit.export
    def identity_string(self, x: str):
        return x

    @torch.jit.export
    def identity_named_tuple(self, x: State):
        return x

    @torch.jit.export
    def similarity(self, x: torch.Tensor, y: torch.Tensor):
        return self.cos_sim(x, y)

    def forward(self, x):
        return self.linear(x)

def main():
    model = DummyModel()
    model.eval()
    scripted_module = torch.jit.script(model)
    name = "dummy_module.ptl"
    scripted_module._save_for_lite_interpreter(name)
    print(f"Saved model to {name}")

if __name__ == "__main__":
    print("PyTorch Version", torch.__version__)
    main()
