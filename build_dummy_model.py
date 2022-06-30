from typing import Dict

import torch
import torch.nn as nn

class DummyModel(nn.Module):
    def __init__(self):
        super().__init__()
        self.linear = nn.Linear(10, 10)
        self.cos_sim = nn.CosineSimilarity()

    @torch.jit.export
    def inference(self, x: torch.Tensor):
        return self.linear(x)

    @torch.jit.export
    def inference_dict(self, x: Dict[str, torch.Tensor]):
        return self.linear(x["x"])

    @torch.jit.export
    def identity(self, x: torch.Tensor):
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